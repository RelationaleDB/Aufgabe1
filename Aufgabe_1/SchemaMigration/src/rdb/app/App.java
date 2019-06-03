/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb.app;

import java.util.ArrayList;
import java.util.List;
import rdb.app.PostgresDB;
import rdb.app.Oracle_Metadata;
import rdb.app.Postgres_Test_Tables;

/**
 *
 * @author a
 */
public class App {
    PostgresDB pgDB;
    Oracle_Metadata orcl_DB_Struct;
    Postgres_Test_Tables postgresTestTables;
    
    public App(){
       pgDB = new PostgresDB();
       orcl_DB_Struct = new Oracle_Metadata();
       postgresTestTables = new Postgres_Test_Tables();
    }
    
    
    private boolean schemaTransfer(String schemaName) {
        boolean success = false;
        if(orcl_DB_Struct.setSchemaName(schemaName)){
            if(orcl_DB_Struct.callDropTables()){
                ArrayList<StringBuilder> sql_orcl = pgDB.callGetSchemaSQL_for_ORCL();
                ArrayList<StringBuilder> sql_pg = pgDB.callGetSchemaSQL_for_PG();
                postgresTestTables.callCreateSchema(sql_pg);
                orcl_DB_Struct.callCreateSchema(sql_orcl);
                success=true;
            }
        }
        return success;
    }
   
    
    private ArrayList<StringBuilder> calculateTransfer(ArrayList<String> typeNamesList, Integer[] productioYear) {
        return pgDB.callCalculateTransfer(typeNamesList, productioYear);
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
    
    public ArrayList<StringBuilder> call_CalculateTransfer(ArrayList<String> typeNamesList, Integer[] productionYear) {
        return calculateTransfer(typeNamesList, productionYear);
    }
    
    public boolean call_startTransfer() {
        return startTransfer();
    }
}
