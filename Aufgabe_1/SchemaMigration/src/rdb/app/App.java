/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb.app;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import rdb.data.DbConnection;
import rdb.data.DbConnectionSingletonFactory;
import rdb.app.PostgresDB;
import rdb.app.OracleDB;

/**
 *
 * @author a
 */
public class App {
    PostgresDB pgDB;
    OracleDB orclDB;
    
    
    public App(){
       pgDB = new PostgresDB();
       orclDB = new OracleDB();
    }
    
    
    private boolean schemaTransfer(String schemaName) {
        boolean success = false;
        if(orclDB.setSchemaName(schemaName)){
            if(orclDB.callDropTables()){
                ArrayList<StringBuilder> sql = pgDB.callGetSchemaSQL();
                orclDB.callCreateSchema(sql);
                for(StringBuilder sb : sql){
                    System.out.println(sb.toString());
                }
                success=true;
            }
        }
        return success;
    }
   
    
    private List<String> berechneTransfer() {
        List<String> list = new ArrayList<String>();
        
        
        
        return list;
    }
    
    private boolean startTransfer() {
        boolean success = false;
        
        return success;
    }
    
    private ArrayList<String> getItemsList_for_ChoiceBoxes(String tableName) {
        return pgDB.callGetItemsList_for_ChoiceBoxes(tableName);
    }
    
    public ArrayList<String> call_ItemsList_for_ChoiceBoxes(String tableName) {
        return getItemsList_for_ChoiceBoxes(tableName);
    }
    
    public boolean call_SchemaTransfer(String schemaName) {
        return schemaTransfer(schemaName);
    }
    
    public List<String> call_berechneTransfer() {
        return berechneTransfer();
    }
    
    public boolean call_startTransfer() {
        return startTransfer();
    }
}
