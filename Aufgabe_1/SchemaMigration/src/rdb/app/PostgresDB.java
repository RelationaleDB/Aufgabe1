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

/**
 *
 * @author a
 */
public class PostgresDB {
    private DbConnectionSingletonFactory dbConFactory;
    private DbConnection dbConPostgres;
    private DatabaseMetaData databaseMetaDataPG;
    private final String[] allTables={"kind_type", "link_type", "info_type", "company_type", "role_type", 
                                      "company_name", "name", "char_name", "title", "person_info", 
                                      "movie_link","movie_info_idx", "movie_info", "movie_companies","cast_info"};
    
    public PostgresDB(){
        dbConFactory = DbConnectionSingletonFactory.getDbConnectionSingletonFactory();
        dbConPostgres = dbConFactory.getDbConnection("postgres");
        getMetaDataConnection();
    }
    
    private void getMetaDataConnection(){
        try {
            databaseMetaDataPG = dbConPostgres.getConnection().getMetaData();
        } catch (SQLException e) {
            System.err.println("Die Verbindung zur Postgres Datenbank (DatabaseSchema) war nicht erfolgreich.");
            e.printStackTrace();
        }
    }
    
     private ArrayList<StringBuilder> getSchemaSQL() {
        ResultSetMetaData rsmd= null;
        
        ArrayList<StringBuilder> listSB = new ArrayList<>();
        StringBuilder temp;
        
        boolean success = false;
        
        for (int i = 0; i < allTables.length; i++) {
            temp = new StringBuilder();
            try(ResultSet rs = databaseMetaDataPG.getColumns(null,null, allTables[i], null);
                    ResultSet rs2 = databaseMetaDataPG.getPrimaryKeys( "" , "" , allTables[i]);
                    ResultSet rs3 = databaseMetaDataPG.getImportedKeys( "" , "" , allTables[i]);)
            {
                temp.append("CREATE TABLE "+allTables[i]+"("+"\n");
                
                while(rs.next()){
                        temp.append(rs.getString("COLUMN_NAME")+"\t"+convertType_Postgres_Oracle(rs.getString("TYPE_NAME"),rs.getInt("COLUMN_SIZE"))+","+"\n");
                }
                
                while(rs2.next()){
                    temp.append("CONSTRAINT "+rs2.getString("PK_NAME")+" PRIMARY KEY"+"("+rs2.getString("COLUMN_NAME")+"),");
                }
                
                while(rs3.next()){
                    temp.append("\n"+"CONSTRAINT "+allTables[i]+"_"+rs3.getString("FK_NAME")+" FOREIGN KEY"+"("+rs3.getString("FKCOLUMN_NAME")+")"
                            +" REFERENCES "+rs3.getString("PKTABLE_NAME")+"("+rs3.getString("PKCOLUMN_NAME")+"),");    
                }
                temp.deleteCharAt(temp.lastIndexOf(","));
                temp.deleteCharAt(temp.lastIndexOf("\n"));
                temp.append(")"+"\n");
                listSB.add(temp);
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        
        return listSB;
        /*for(StringBuilder temp2 : listSB){
                System.out.println(temp2.toString()+"\n\n");
            }*/
    }
    
    
    private String convertType_Postgres_Oracle(String s, int size){
        String string = "";
        switch(s){
            case "serial":
                string = "number(10)";
                break;
            case "int4":
                string = "number(10)";
                break;
            case "varchar":
                string = "varchar2"+"("+size+")";
                break;
            case "text":
                string = "clob";
                break;
        }
        return string;
    }
    
    private ArrayList<String> getItemsList_for_ChoiceBoxes(String tableName) {
        ArrayList<String> list = null;  
        ArrayList<Integer> list2 = new ArrayList<>(); 
            try(ResultSet rs= dbConPostgres.getConnection().createStatement().executeQuery("SELECT * from "+tableName+" ");)
            {
                list=new ArrayList<>();
                while(rs.next()){
                    list.add(rs.getString(2));
                }
                list.add(0, "");
           }catch(SQLException ex){
               ex.printStackTrace();
        }
        return list;
    }
    
    public ArrayList<String> callGetItemsList_for_ChoiceBoxes(String tableName){
        ArrayList<String> list = getItemsList_for_ChoiceBoxes(tableName);
        return list;
    }
    
    public ArrayList<StringBuilder> callGetSchemaSQL(){
        ArrayList<StringBuilder> sql = getSchemaSQL();
        return sql;
    }
    
    
}
