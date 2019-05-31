
package rdb.app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author a
 */
/*
public class App {
        
    private final String[] typeTableNames = {"company_type","link_type","kind_type","info_type","role_type"};
    private final String[] typeTableNamesRefTables = {"movie_companies","movie_link","title","person_info",
            "movie_info_idx","movie_info","cast_info"};
    private final String[] allTables={"company_type","link_type","kind_type","info_type","role_type",
            "movie_companies","movie_link","title","person_info","movie_info_idx",
            "movie_info","cast_info","char_name","company_name","name"}; 
    
    ArrayList<Map<String,String>> list_TableAndTypeNames_from_UserChoice = new ArrayList<>();
    
    private void createSQLQuery_with_TypeChoices(){
        ArrayList<Map<String,String>> list_of_SQLQuery_to_typeRefTables = null;
        Map<String,String> map;
        for(String s: typeTableNamesRefTables){
            map = new HashMap<String, String>();
            map.put(s,getSQLQueryfortypeRefTables(s));
        }
    }
    
    private String getSQLQueryfortypeRefTables(String  refTableName){
        String SQL = "SELECT * from "+refTableName+" ";
        ArrayList<String> list = null;
        for(Map<String,String> map : list_TableAndTypeNames_from_UserChoice){
            for(Map.Entry<String,String> entry : map.entrySet()){
                if(entry.getKey().equals(refTableName))
                    list.add(entry.getValue());
            }
        }
        if(!list.isEmpty())
            SQL = SQL.concat("WHERE "+getTableNameByValue_ForTypeTables(list.get(0))+" IN(SELECT id FROM ");
            for(String s : list)
                SQL = SQL.concat("WHERE person_role_id IN(SELECT id FROM role_type WHERE role = '"
                    +cast_info_Role_type1+"' OR role = '"+cast_info_Role_type1+"')");
    }
    
    private void get_List_Of_TypeValuesTableNames(){
        list_TableAndTypeNames_from_UserChoice = new ArrayList<>();
        Map<String,String> map = null;
        for(String s : observableList){
            map = new HashMap<String,String>();
            map.put(getTableNameByValue_ForTypeTables(s), s);
            for(Map.Entry<String,String> entry : map.entrySet())
                System.out.println("Key: "+entry.getKey()+"\tValue"+entry.getValue());
            list_TableAndTypeNames_from_UserChoice.add(map);
        }
    }
    
    private int getRowCountFromResulSet(){
        return 0;}

    public String[] getTypeTableNames() {
        return typeTableNames;
    }

    public String[] getTypeTableNamesRefTables() {
        return typeTableNamesRefTables;
    }

    public String[] getAllTables() {
        return allTables;
    }

    public void setList_TableAndTypeNames_from_UserChoice(ArrayList<Map<String, String>> list_TableAndTypeNames_from_UserChoice) {
        this.list_TableAndTypeNames_from_UserChoice = list_TableAndTypeNames_from_UserChoice;
    }
    
    
    
}

*/