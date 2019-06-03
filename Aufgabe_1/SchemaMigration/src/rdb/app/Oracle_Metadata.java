/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb.app;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.util.Pair;
import rdb.data.DbConnection;
import rdb.data.DbConnectionSingletonFactory;

/**
 *
 * @author a
 */
public class Oracle_Metadata {
    private DbConnectionSingletonFactory dbConFactory;
    private DbConnection dbConOracle;
    private DatabaseMetaData databaseMetaDataORCL;
    private String schemaName;

    
    ArrayList<String> tableNamesList_Oracle=null;
    ArrayList<Pair<String, String>> foreignKeysList_Oracle=null;
    
    public Oracle_Metadata(){
        dbConFactory = DbConnectionSingletonFactory.getDbConnectionSingletonFactory();
        dbConOracle = dbConFactory.getDbConnection("oracle");
        getMetaDataConnection();
    }
    
    private void getMetaDataConnection(){
        try {
            databaseMetaDataORCL = dbConOracle.getConnection().getMetaData();
        } catch (SQLException e) {
            System.err.println("Die Verbindung zur Oracle Datenbank (DatabaseSchema) war nicht erfolgreich.");
            e.printStackTrace();
        }
    }
    
    private void oracle_getTableNames(){
       String tableName= "";
       tableNamesList_Oracle = new ArrayList<String>();
        

        try(ResultSet rs = databaseMetaDataORCL.getTables(null, null, null, new String[]{"TABLE"});)
        {
            
            while(rs.next())
            {
                if(rs.getString("TABLE_SCHEM").contains(schemaName)){
                    tableName = rs.getString("TABLE_NAME");
                    tableNamesList_Oracle.add(tableName);
                }
            }
        }catch(SQLException ex){
        ex.printStackTrace();
        }
        if(tableNamesList_Oracle.isEmpty()) 
            System.err.println("Es sind keine Tabellen vorhanden.");
    }
    
    private void oracle_getConstraints(){
       foreignKeysList_Oracle = new ArrayList<Pair<String,String>>();
        
        for(String table : tableNamesList_Oracle){
            try(ResultSet rs = databaseMetaDataORCL.getImportedKeys( "" , "" , table);)
            {
                    while(rs.next())
                    {
                        Pair <String,String> pair = new Pair(rs.getString("FKTABLE_NAME"),rs.getString("FK_NAME"));
                        foreignKeysList_Oracle.add(pair);
                    }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        if(tableNamesList_Oracle.isEmpty()) 
            System.err.println("Es sind keine Fremdschlüssel vorhanden.");
    }
    
    private void oracle_Drop_Constraints(){
        
        if(!tableNamesList_Oracle.isEmpty()&&!foreignKeysList_Oracle.isEmpty()){
            try(Statement stmt = dbConOracle.getConnection().createStatement();)
            {
                for(Pair pair : foreignKeysList_Oracle){
                    stmt.executeUpdate("ALTER TABLE "+pair.getKey()+" DROP CONSTRAINT "+"\""
                                    +pair.getValue()+"\"");
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.err.println("Es sind keine Fremdschlüssel zum löschen vorhanden.");
        }
    }
    
    private void oracle_Drop_Tables(){
        if(!tableNamesList_Oracle.isEmpty()){
            try(Statement stmt = dbConOracle.getConnection().createStatement();)
            {
                for(String tableName : tableNamesList_Oracle){
                    stmt.execute("drop table "+tableName);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.err.println("Es sind keine Tabellen zum löschen vorhanden.");
        }
    }
    
    private void oracle_createSchema(ArrayList<StringBuilder> sql){
            try(Statement stmt = dbConOracle.getConnection().createStatement();)
            {
                for(StringBuilder sql_stmt : sql)
                    stmt.addBatch(sql_stmt.toString()); 
                stmt.executeBatch();
            }catch(SQLException ex){
                    ex.printStackTrace();
            }
        } 
    
    public boolean callDropTables(){
        boolean result = false;
        try{
            oracle_getTableNames();
            oracle_getConstraints();
            oracle_Drop_Constraints();
            oracle_Drop_Tables();
            result=true;
        }catch(Exception ex){
            System.err.println("Das Löschen der Tabellen war leider kein Erfolg...");
        }
        return result;
    }
    
    public void callCreateSchema(ArrayList<StringBuilder> sql){
        oracle_createSchema(sql);
    }
    
    public boolean setSchemaName(String schemaName) {
        boolean result=false;
        try
        {
        ResultSet schemas = databaseMetaDataORCL.getSchemas();
        while(schemas.next()){
            if(schemaName.toUpperCase().equals(schemas.getString(1).toUpperCase())){
                this.schemaName = schemaName.toUpperCase();
                result=true;
            }
        }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }
    
}
