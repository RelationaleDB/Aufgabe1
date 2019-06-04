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
import java.util.TreeMap;
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
    private final String[] typeTables = {"kind_type", "link_type", "info_type", "company_type", "role_type"};
    private final String[] typeTableNames = {"title", "movie_link", "movie_info_idx", "movie_info", "movie_companies", "person_info", "cast_info"};
    
    private final String[] allTables = {"kind_type", "title", "link_type", "movie_link", "info_type", "movie_info_idx", 
        "movie_info","company_type", "company_name", "movie_companies", "name", "person_info",  "role_type",
          "char_name", "cast_info"};
    
    private final String[] allTables_without_typeTables = {"title", "movie_link", "movie_info_idx", 
        "movie_info","company_name", "movie_companies", "name", "person_info", "char_name", "cast_info"};
    
    private final String[] allTables_inOrder = {"kind_type", "link_type", "info_type", "company_type", "role_type","title", "movie_link", "movie_info_idx", 
        "movie_info","movie_companies","person_info", "cast_info", "company_name",  "name", "char_name"};

    
    public PostgresDB() {
        dbConFactory = DbConnectionSingletonFactory.getDbConnectionSingletonFactory();
        dbConPostgres = dbConFactory.getDbConnection("postgres");
        getMetaDataConnection();
    }

    private void getMetaDataConnection() {
        try {
            databaseMetaDataPG = dbConPostgres.getConnection().getMetaData();
        } catch (SQLException e) {
            System.err.println("Die Verbindung zur Postgres Datenbank (DatabaseSchema) war nicht erfolgreich.");
            e.printStackTrace();
        }
    }
    
    private ArrayList<StringBuilder> dropTables_PG() {
        
        ArrayList<StringBuilder> sql = new ArrayList<>();
        
        for (int i = allTables.length-1; i >= 0; i--) {
            StringBuilder newStringBuilder = new StringBuilder();
            newStringBuilder.append("DROP TABLE IF EXISTS "+ allTables[i] +"_test CASCADE; ");
            sql.add(newStringBuilder);
        }
        
        return sql;
    }

    private ArrayList<StringBuilder> getSchemaSQL_for_ORCL() {
        ResultSetMetaData rsmd = null;

        ArrayList<StringBuilder> listSB = new ArrayList<>();
        StringBuilder temp;

        boolean success = false;

        for (int i = 0; i < allTables.length; i++) {
            temp = new StringBuilder();
            try (ResultSet rs = databaseMetaDataPG.getColumns(null, null, allTables[i], null);
                    ResultSet rs2 = databaseMetaDataPG.getPrimaryKeys("", "", allTables[i]);
                    ResultSet rs3 = databaseMetaDataPG.getImportedKeys("", "", allTables[i]);) {
                    temp.append("CREATE TABLE " + allTables[i] + "(" + "\n");
                while (rs.next()) {
                        temp.append(rs.getString("COLUMN_NAME") + "\t" + convertType_Postgres_Oracle(rs.getString("TYPE_NAME"), rs.getInt("COLUMN_SIZE")) + "," + "\n");
                }

                while (rs2.next()) {
                    temp.append("CONSTRAINT " + rs2.getString("PK_NAME") + " PRIMARY KEY" + "(" + rs2.getString("COLUMN_NAME") + "),");
                }

                while (rs3.next()) {
                    temp.append("\n" + "CONSTRAINT " + allTables[i] + "_" + rs3.getString("FK_NAME") + " FOREIGN KEY" + "(" + rs3.getString("FKCOLUMN_NAME") + ")"
                            + " REFERENCES " + rs3.getString("PKTABLE_NAME") + "(" + rs3.getString("PKCOLUMN_NAME") + "),");
                }
                temp.deleteCharAt(temp.lastIndexOf(","));
                temp.deleteCharAt(temp.lastIndexOf("\n"));
                temp.append(")" + "\n");
                listSB.add(temp);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
/*for(StringBuilder sb : listSB)
                System.out.println(sb.toString());*/
        return listSB;
    }
    
    private ArrayList<StringBuilder> getSchemaSQL_for_PG() {
        ResultSetMetaData rsmd = null;

        ArrayList<StringBuilder> listSB = new ArrayList<>();
        StringBuilder temp;

        boolean success = false;

        for (int i = 0; i < allTables.length; i++) {
            temp = new StringBuilder();
            try (ResultSet rs = databaseMetaDataPG.getColumns(null, null, allTables[i], null);
                    ResultSet rs2 = databaseMetaDataPG.getPrimaryKeys("", "", allTables[i]);
                    ResultSet rs3 = databaseMetaDataPG.getImportedKeys("", "", allTables[i]);) {
                    temp.append("CREATE TABLE " + allTables[i]+"_test" + "(" + "\n");
                while (rs.next()) {
                        temp.append(rs.getString("COLUMN_NAME") + "\t" + rs.getString("TYPE_NAME") + "," + "\n");
                }

                while (rs2.next()) {
                    temp.append("CONSTRAINT " + rs2.getString("PK_NAME") +"_test"+ " PRIMARY KEY" + "(" + rs2.getString("COLUMN_NAME") + "),");
                }

                while (rs3.next()) {
                    //temp.append("\n" + "CONSTRAINT " + allTables[i] + "_" + rs3.getString("FK_NAME") +"_test"+ " FOREIGN KEY" + "(" + rs3.getString("FKCOLUMN_NAME") + ")"
                    //        + " REFERENCES " + rs3.getString("PKTABLE_NAME")+"_test"+ "(" + rs3.getString("PKCOLUMN_NAME") + "),");
                }
                temp.deleteCharAt(temp.lastIndexOf(","));
                temp.deleteCharAt(temp.lastIndexOf("\n"));
                temp.append(")" + "\n");
                listSB.add(temp);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
/*for(StringBuilder sb : listSB)
                System.out.println(sb.toString());*/
        return listSB;
    }

    private String convertType_Postgres_Oracle(String s, int size) {
        String string = "";
        switch (s) {
            case "serial":
                string = "number(10)";
                break;
            case "int4":
                string = "number(10)";
                break;
            case "varchar":
                string = "varchar2" + "(" + size + ")";
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
        try (ResultSet rs = dbConPostgres.getConnection().createStatement().executeQuery("SELECT * from " + tableName + " ");) {
            list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getString(2));
            }
            list.add(0, "");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private ArrayList<StringBuilder> calculateTransfer(ArrayList<String> typeNamesList, Integer[] productionYear) {
        ArrayList<StringBuilder> result = new ArrayList<>();
        
        ArrayList<StringBuilder> sql_list = calculateTransfer_SQL(typeNamesList, productionYear);

        
        for (StringBuilder sb : sql_list) {
            System.out.println(sb.toString());
        }
        
        try(Statement stmt = dbConPostgres.getConnection().createStatement();)
        {
            for (int i = 0; i < sql_list.size(); i++) {
                stmt.addBatch(sql_list.get(i).toString());
            }
            stmt.executeBatch();
        }catch(SQLException ex){
                    ex.printStackTrace();
            }

        return result;
    
    }
    
    
    private ArrayList<StringBuilder> calculateTransfer_SQL(ArrayList<String> typeNamesList, Integer[] productionYear) {

        String[] allTables_without_typeTables = {"title", "movie_link", "movie_info_idx", 
        "movie_info","company_name", "movie_companies", "name", "person_info", "char_name", "cast_info"};
        
        String productionsYear1 = productionYear[0] == null ? "" : productionYear[0].toString();
        String productionsYear2 = productionYear[1] == null ? "" : productionYear[1].toString();
        
        StringBuilder sql_kind_type = new StringBuilder("SELECT * from kind_type ");
        StringBuilder sql_link_type = new StringBuilder("SELECT * from link_type ");
        StringBuilder sql_info_type = new StringBuilder("SELECT * from info_type ");
        StringBuilder sql_company_type = new StringBuilder("SELECT * from company_type ");
        StringBuilder sql_role_type = new StringBuilder("SELECT * from role_type ");
        StringBuilder sql_title = new StringBuilder("SELECT * from title WHERE kind_id IN(SELECT id FROM kind_type WHERE kind = '");
        StringBuilder sql_movie_link = new StringBuilder("SELECT * from movie_link WHERE link_type_id IN(SELECT id FROM link_type WHERE link = '");
        StringBuilder sql_movie_info_idx = new StringBuilder("SELECT * from movie_info_idx WHERE info_type_id IN(SELECT id FROM info_type WHERE info = '");
        StringBuilder sql_movie_info = new StringBuilder("SELECT * from movie_info WHERE info_type_id IN(SELECT id FROM info_type WHERE info = '");
        StringBuilder sql_movie_companies = new StringBuilder("SELECT * from movie_companies WHERE company_type_id IN(SELECT id FROM company_type WHERE kind = '");
        StringBuilder sql_person_info = new StringBuilder("SELECT * from person_info WHERE info_type_id IN(SELECT id FROM info_type WHERE info = '");
        StringBuilder sql_cast_info = new StringBuilder("SELECT * from cast_info WHERE role_id IN(SELECT id FROM role_type WHERE role = '");
        StringBuilder sql_company_name = new StringBuilder("SELECT * from company_name WHERE id IN(SELECT company_id FROM movie_companies WHERE id=company_id) ");
        StringBuilder sql_name = new StringBuilder("SELECT * from name WHERE id IN(SELECT person_role_id FROM cast_info WHERE id=person_role_id) ");
        StringBuilder sql_char_name = new StringBuilder("SELECT * from char_name WHERE id IN(SELECT person_id FROM cast_info WHERE id=person_id) ");
        

        
        int sql_movie_companies_count, sql_movie_link_count, sql_title_count, sql_person_info_count, sql_movie_info_count, sql_movie_info_idx_count, sql_cast_info_count;
        sql_movie_companies_count = sql_movie_link_count = sql_title_count = sql_person_info_count = sql_movie_info_count = sql_movie_info_idx_count = sql_cast_info_count = 0;

        for (String s : typeNamesList) {
            String tableName = getTableNames_for_calculateTransfer(s);
            if (tableName.equals("company_type")) {
                sql_movie_companies.append(s + "' OR kind = '");
                sql_movie_companies_count++;
            }
            if (tableName.equals("link_type")) {
                sql_movie_link.append(s + "' OR link = '");
                sql_movie_link_count++;
            }
            if (tableName.equals("kind_type")) {
                sql_title.append(s + "' OR kind = '");
                sql_title_count++;
            }
            if (tableName.equals("info_type")) {
                sql_person_info.append(s + "' OR info = '");
                sql_movie_info.append(s + "' OR info = '");
                sql_movie_info_idx.append(s + "' OR info = '");
                sql_person_info_count++;
                sql_movie_info_count++;
                sql_movie_info_idx_count++;
            }
            if (tableName.equals("role_type")) {
                sql_cast_info.append(s + "' OR role = '");
                sql_cast_info_count++;
            }
        }

        if (sql_movie_companies_count > 0) {
            removeStringFromStringbuilder(sql_movie_companies, "OR kind = '");
            sql_movie_companies.append(")");
        } else {
            sql_movie_companies.delete(sql_movie_companies.indexOf("WHERE"), sql_movie_companies.length());
        }
        sql_movie_companies.append(" AND movie_id IN(SELECT id FROM title_test WHERE id = movie_id) ");
        if (sql_movie_link_count > 0) {
            removeStringFromStringbuilder(sql_movie_link, "OR link = '");
            sql_movie_link.append(")");
        } else {
            sql_movie_link.delete(sql_movie_link.indexOf("WHERE"), sql_movie_link.length());
        }
        if(sql_movie_link_count > 0)
            sql_movie_link.append(" AND movie_id IN(SELECT id FROM title_test WHERE id = movie_id) OR linked_movie_id IN(SELECT id FROM title_test WHERE id=linked_movie_id) ");
        else
            sql_movie_link.append(" WHERE movie_id IN(SELECT id FROM title_test WHERE id = movie_id) OR linked_movie_id IN(SELECT id FROM title_test WHERE id=linked_movie_id) ");    
            
        if (sql_title_count > 0) {
            removeStringFromStringbuilder(sql_title, "OR kind = '");
            sql_title.append(")");
            if (productionsYear1.length()>3 && productionsYear2.length()>3) {
                sql_title.append("AND production_year between " + productionsYear1 + " and " + productionsYear2);
            }
        } else {
            sql_title.setLength(20);
            if (productionsYear1.length()>3 && productionsYear2.length()>3)
                sql_title.append("WHERE production_year between " + productionsYear1 + " and " + productionsYear2);
        }

        if (sql_person_info_count > 0) {
            removeStringFromStringbuilder(sql_person_info, "OR info = '");
            sql_person_info.append(")");
        } else {
            sql_person_info.delete(sql_person_info.indexOf("WHERE"), sql_person_info.length());
        }

        if (sql_movie_info_idx_count > 0) {
            removeStringFromStringbuilder(sql_movie_info_idx, "OR info = '");
            sql_movie_info_idx.append(")");
        } else {
            sql_movie_info_idx.delete(sql_movie_info_idx.indexOf("WHERE"), sql_movie_info_idx.length());
        }
        if(sql_movie_info_idx_count > 0)
            sql_movie_info_idx.append(" AND movie_id IN(SELECT id FROM title_test WHERE id = movie_id) ");
        else
            sql_movie_info_idx.append(" WHERE movie_id IN(SELECT id FROM title_test WHERE id = movie_id) ");
        
        if (sql_movie_info_count > 0) {
            removeStringFromStringbuilder(sql_movie_info, "OR info = '");
            sql_movie_info.append(")");
        } else {
            sql_movie_info.delete(sql_movie_info.indexOf("WHERE"), sql_movie_info.length());
        }
        if(sql_movie_info_count > 0)
            sql_movie_info.append(" AND movie_id IN(SELECT id FROM title_test WHERE id = movie_id) ");
        else
            sql_movie_info.append(" WHERE movie_id IN(SELECT id FROM title_test WHERE id = movie_id) ");
        
        if (sql_cast_info_count > 0) {
            removeStringFromStringbuilder(sql_cast_info, "OR role = '");
            sql_cast_info.append(")");
        } else {
            sql_cast_info.delete(sql_cast_info.indexOf("WHERE"), sql_cast_info.length());
        }
        if(sql_cast_info_count > 0)
            sql_cast_info.append(" AND movie_id IN(SELECT id FROM title_test WHERE id = movie_id) ");
        else
            sql_cast_info.append(" WHERE movie_id IN(SELECT id FROM title_test WHERE id = movie_id) ");
        
                ;

        
        
          
        ArrayList<StringBuilder> sql_list = new ArrayList<StringBuilder>();
        
        sql_list.add(sql_kind_type);
        sql_list.add(sql_link_type);
        sql_list.add(sql_info_type);
        sql_list.add(sql_company_type);
        sql_list.add(sql_role_type);
        sql_list.add(sql_title);
        sql_list.add(sql_movie_link);
        sql_list.add(sql_movie_info_idx);
        sql_list.add(sql_movie_info);
        sql_list.add(sql_movie_companies);
        sql_list.add(sql_person_info);
        sql_list.add(sql_cast_info);
        sql_list.add(sql_company_name); 
        sql_list.add(sql_name); 
        sql_list.add(sql_char_name);
        
        for (int i = 0; i < allTables_inOrder.length; i++) {
            for (int j = 0; j < sql_list.size(); j++) {
                if(i==j)
                    sql_list.get(j).insert(0, "INSERT INTO "+allTables_inOrder[i]+"_test ");
            }
        }
            
        
        return sql_list;
    }
    
    

    private void removeStringFromStringbuilder(StringBuilder sb, String remove) {
        int i = sb.lastIndexOf(remove);
        if (i != -1) {
            sb.delete(i, i + remove.length());
        }
    }

    private String getTableNames_for_calculateTransfer(String value) {
        String tableName = null;
        for (int i = 0; i < typeTables.length; i++) {
            try (ResultSet rs = dbConPostgres.getConnection().createStatement().executeQuery("SELECT * from " + typeTables[i] + " ");) {
                while (rs.next()) {
                    if (rs.getString(2).equals(value)) {
                        return typeTables[i];
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return tableName;
    }

    public ArrayList<String> callGetItemsList_for_ChoiceBoxes(String tableName) {
        return getItemsList_for_ChoiceBoxes(tableName);
    }

    public ArrayList<StringBuilder> callGetSchemaSQL_for_ORCL() {
      return getSchemaSQL_for_ORCL();
    }
    
    public ArrayList<StringBuilder> callGetSchemaSQL_for_PG() {
      return getSchemaSQL_for_PG();
    }
    
    public ArrayList<StringBuilder> callDropTables_PG(){
      return dropTables_PG();
    }

    public ArrayList<StringBuilder> callCalculateTransfer(ArrayList<String> typeNamesList, Integer[] productionYear) {
        return calculateTransfer(typeNamesList, productionYear);
    }
}
