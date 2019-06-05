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
import rdb.app.PostgresDB;

/**
 *
 * @author a
 */
public class Postgres_Test_Tables {
    private DbConnectionSingletonFactory dbConFactory;
    private DbConnection dbConPostgres;
    private DatabaseMetaData databaseMetaDataORCL;
    PostgresDB postgresDB;

    
    ArrayList<String> tableNamesList_Oracle=null;
    ArrayList<Pair<String, String>> foreignKeysList_Oracle=null;
    
    public Postgres_Test_Tables(){
        dbConFactory = DbConnectionSingletonFactory.getDbConnectionSingletonFactory();
        dbConPostgres = dbConFactory.getDbConnection("postgres");
        getMetaDataConnection();
        postgresDB = new PostgresDB();
    }
    
    private void getMetaDataConnection(){
        try {
            databaseMetaDataORCL = dbConPostgres.getConnection().getMetaData();
        } catch (SQLException e) {
            System.err.println("Die Verbindung zur Oracle Datenbank (DatabaseSchema) war nicht erfolgreich.");
            e.printStackTrace();
        }
    }
    
    private void postgres_dropTables(){
        ArrayList<StringBuilder> sql = postgresDB.callDropTables_PG();
        for(StringBuilder sb : sql)
                System.out.println(sb.toString());
        try(Statement stmt = dbConPostgres.getConnection().createStatement();)
            {
                for(StringBuilder sql_stmt : sql)
                    stmt.addBatch(sql_stmt.toString()); 
                stmt.executeBatch();
            }catch(SQLException ex){
                    ex.printStackTrace();
            } 
    }
    
    private void postgres_createSchema(ArrayList<StringBuilder> sql){
       ArrayList<StringBuilder> sqlFK = postgresDB.callGetSQLForeignKeysforPG();
        for(StringBuilder sb : sql)
                System.out.println(sb.toString());
        postgres_dropTables();
            try(Statement stmt = dbConPostgres.getConnection().createStatement();)
            {
                for(StringBuilder sql_stmt : sql)
                    stmt.addBatch(sql_stmt.toString()); 
                stmt.executeBatch();
            }catch(SQLException ex){
                    ex.printStackTrace();
            } 
            
        }
    
     private void postgres_createFK(){
       ArrayList<StringBuilder> sqlFK = postgresDB.callGetSQLForeignKeysforPG();
        for(StringBuilder sb : sqlFK)
                System.out.println(sb.toString());
            try(Statement stmt = dbConPostgres.getConnection().createStatement();)
            {
                for(StringBuilder sql_stmt : sqlFK)
                    stmt.addBatch(sql_stmt.toString()); 
                stmt.executeBatch();
            }catch(SQLException ex){
                    ex.printStackTrace();
            } 
            
        }
    
    private void add_ForeignKeysToTestTables(){
        postgresDB.callGetSQLForeignKeysforPG();
    }
    
    public void callCreateSchema(ArrayList<StringBuilder> sql){
        postgres_createSchema(sql);
    }
    
    public void callCreateFK(){
        postgres_createFK();
    }
    
}
