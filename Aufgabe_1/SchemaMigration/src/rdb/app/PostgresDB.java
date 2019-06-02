/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb.app;

import java.lang.StringBuilder;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private final String[] typeTables = {"company_type","link_type","kind_type","info_type","role_type"};
    private final String[] typeTableNames={"title", "person_info", "movie_link","movie_info_idx", "movie_info", "movie_companies","cast_info"};
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
    

    private ArrayList<StringBuilder> calculateTransfer(ArrayList<String> typeNamesList, Integer[] productionYear){
        ArrayList<StringBuilder> result = new ArrayList<>();
        ArrayList<Map<String,StringBuilder>> sql = calculateTransferSQL(typeNamesList, productionYear);
        
       
        
        for (int i = 0; i < sql.size(); i++) {
            for(Map.Entry<String, StringBuilder> map : sql.get(i).entrySet()){
            }
        }
        
        
        return result;
    }
    
    private ArrayList<Map<String, StringBuilder>>calculateTransferSQL(ArrayList<String> typeNamesList, Integer[] productionYear){
        
        ArrayList<StringBuilder> sql;
        
        String productionsYear1 = (productionYear[0]==null&&productionYear[0]>productionYear[1])?"":productionYear[0].toString();
        String productionsYear2 = productionYear[1]==null?"":productionYear[1].toString();
        
        StringBuilder sql_movie_companies = new StringBuilder("SELECT * from movie_companies WHERE company_type_id IN(SELECT id FROM company_type WHERE kind = '");
        StringBuilder sql_movie_link = new StringBuilder("SELECT * from movie_link WHERE link_type_id IN(SELECT id FROM link_type WHERE link = '");
        StringBuilder sql_title = new StringBuilder("SELECT * from title WHERE kind_id IN(SELECT id FROM kind_type WHERE kind = '");
        StringBuilder sql_person_info = new StringBuilder("SELECT * from person_info WHERE info_type_id IN(SELECT id FROM info_type WHERE info = '");
        StringBuilder sql_movie_info_idx = new StringBuilder("SELECT * from movie_info_idx WHERE info_type_id IN(SELECT id FROM info_type WHERE info = '");
        StringBuilder sql_movie_info = new StringBuilder("SELECT * from movie_info WHERE info_type_id IN(SELECT id FROM info_type WHERE info = '");
        StringBuilder sql_cast_info = new StringBuilder("SELECT * from cast_info WHERE role_id IN(SELECT id FROM role_type WHERE role = '");
        
        int sql_movie_companies_count, sql_movie_link_count, sql_title_count, sql_person_info_count, sql_movie_info_count, sql_movie_info_idx_count, sql_cast_info_count;
        sql_movie_companies_count= sql_movie_link_count= sql_title_count= sql_person_info_count= sql_movie_info_count= sql_movie_info_idx_count= sql_cast_info_count=0;
        
        for(String s : typeNamesList){
            String tableName = getTableNames_for_calculateTransfer(s);
                if (tableName.equals("company_type")){
                    sql_movie_companies.append(s+"' OR kind = '");
                    sql_movie_companies_count++;
                }
                if (tableName.equals("link_type")){
                    sql_movie_link.append(s+"' OR link = '");
                    sql_movie_link_count++;
                }
                if (tableName.equals("kind_type")){
                    sql_title.append(s+"' OR kind = '");
                    sql_title_count++;
                }
                if (tableName.equals("info_type")){
                    sql_person_info.append(s+"' OR info = '");
                    sql_movie_info.append(s+"' OR info = '");    
                    sql_movie_info_idx.append(s+"' OR info = '");
                    sql_person_info_count++;
                    sql_movie_info_count++;
                    sql_movie_info_idx_count++;
                }
                if (tableName.equals("role_type")){
                    sql_cast_info.append(s+"' OR role = '");
                    sql_cast_info_count++;
                }
            }
        
        if(sql_movie_companies_count>0){
            removeStringFromStringbuilder(sql_movie_companies,"OR kind = '");
            sql_movie_companies.append(")");
        }else sql_movie_companies.delete(sql_movie_companies.indexOf("WHERE"),sql_movie_companies.length());
        System.out.println("sql_movie_companies - length: "+sql_movie_companies.length());
        
        if(sql_movie_link_count>0){
            removeStringFromStringbuilder(sql_movie_link,"OR link = '");
            sql_movie_link.append(")");
        }else sql_movie_link.delete(sql_movie_link.indexOf("WHERE"),sql_movie_link.length());
        System.out.println("sql_movie_link - length: "+sql_movie_link.length());
        
        if(sql_title_count>0){
            removeStringFromStringbuilder(sql_title,"OR kind = '");
            sql_title.append(")");
            if(productionYear[0]!=null&&productionYear[1]!=null)
                sql_title.append("AND production_year between "+productionsYear1+" and "+productionsYear2);
        }else{
            sql_title.setLength(20);
            sql_title.append("WHERE production_year between "+productionsYear1+" and "+productionsYear2);
        }
        System.out.println("sql_title - length: "+sql_title.length());
        
        if(sql_person_info_count>0){
            removeStringFromStringbuilder(sql_person_info,"OR info = '");
            sql_person_info.append(")");
        }else sql_person_info.delete(sql_person_info.indexOf("WHERE"),sql_person_info.length());
        System.out.println("sql_person_info - length: "+sql_person_info.length());
        
        if(sql_movie_info_idx_count>0){
            removeStringFromStringbuilder(sql_movie_info_idx,"OR info = '");
            sql_movie_info_idx.append(")");
        }else sql_movie_info_idx.delete(sql_movie_info_idx.indexOf("WHERE"),sql_movie_info_idx.length());
        System.out.println("sql_movie_info_idx - length: "+sql_movie_info_idx.length());
        
        if(sql_movie_info_count>0){
            removeStringFromStringbuilder(sql_movie_info,"OR info = '");
            sql_movie_info.append(")");
        }else sql_movie_info.delete(sql_movie_info.indexOf("WHERE"),sql_movie_info.length());
        System.out.println("sql_movie_info - length: "+sql_movie_info.length());
        
        if(sql_cast_info_count>0){
            removeStringFromStringbuilder(sql_cast_info,"OR role = '");
            sql_cast_info.append(")");
        }else sql_cast_info.delete(sql_cast_info.indexOf("WHERE"),sql_cast_info.length());
        System.out.println("sql_cast_info - length: "+sql_cast_info.length());
        
        sql=new ArrayList<>(){{add(sql_title);add(sql_person_info);add(sql_movie_link);add(sql_movie_info_idx); add(sql_movie_info); add(sql_movie_companies);add(sql_cast_info);}};
        
        ArrayList<Map<String,StringBuilder>> sql_list = new ArrayList<>();
        
        for (int i = 0; i < sql.size(); i++) {
            Map<String, StringBuilder> map = new HashMap<String, StringBuilder>();
            map.put(typeTableNames[i], sql.get(i));
            sql_list.add(map);        
        }
        
        
        for(StringBuilder sb : sql)
            System.out.println(sb.toString());
        return sql_list;
    }
    private void removeStringFromStringbuilder(StringBuilder sb, String remove){
         int i = sb.lastIndexOf(remove);
         if (i != -1) {
            sb.delete(i, i + remove.length());
         }
    }
    
    private String getTableNames_for_calculateTransfer(String value){
        String tableName = null;
        for (int i = 0;i< typeTables.length; i++) {
           try(ResultSet rs= dbConPostgres.getConnection().createStatement().executeQuery("SELECT * from "+typeTables[i]+" ");)
            { 
                 while(rs.next()){
                    if(rs.getString(2).equals(value))
                           return typeTables[i];
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        return tableName;
    }
    
    public ArrayList<String> callGetItemsList_for_ChoiceBoxes(String tableName){
        return getItemsList_for_ChoiceBoxes(tableName);
    }
    
    public ArrayList<StringBuilder> callGetSchemaSQL(){
        return getSchemaSQL();
    }
    
    public ArrayList<StringBuilder> callCalculateTransfer(ArrayList<String> typeNamesList, Integer[] productionYear){
        return calculateTransfer(typeNamesList, productionYear);
    }
}
