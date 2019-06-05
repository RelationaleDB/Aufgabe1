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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import rdb.data.DbConnection;
import rdb.data.DbConnectionSingletonFactory;



/**
 *
 * @author a
 */
public class CopyData {
    
    private DbConnectionSingletonFactory dbConFactory;
    private DbConnection dbConPostgres;
    private DatabaseMetaData databaseMetaDataPG;
    
    
    
    
    ArrayList<Map<String, Object>> cast_info_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> char_name_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> company_name_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> company_type_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> info_type_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> kind_type_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> link_type_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> movie_companies_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> movie_info_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> movie_info_idx_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> movie_link_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> name_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> person_info_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> role_type_table = new ArrayList<Map<String,Object>>();
    ArrayList<Map<String, Object>> title_table = new ArrayList<Map<String,Object>>();
    
    
    
    
    
    
    public CopyData() {
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
    
    private void cast_info_getData() {
        int index=1;
        String selectTableSQL =  
                "SELECT id, person_id, movie_id, "
                + "person_role_id, note, nr_order, role_id from cast_info ";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 cast_info_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 Integer person_id = rs.getInt("person_id");
                 Integer movie_id = rs.getInt("movie_id");
                 Integer person_role_id = rs.getInt("person_role_id");
                 String note = rs.getString("note");
                 Integer nr_order = rs.getInt("nr_order");
                 Integer role_id = rs.getInt("role_id");
                 
                 row.put("id", id);
                 row.put("person_id", person_id);
                 row.put("movie_id", movie_id);
                 row.put("person_role_id", person_role_id);
                 row.put("nr_order", nr_order);
                 row.put("role_id", role_id);
                
                 
                 for(Entry<String, Object> cell : row.entrySet()){
                     System.out.println("--------------------"+index+"------------------------------");
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
                 }
                 index++;
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void char_name_getData() {
        String selectTableSQL =  
                "SELECT id, name, imdb_index, imdb_id, name_pcode_nf, "
                + "surname_pcode, md5sum from char_name ";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 char_name_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 String name = rs.getString("name");
                 String imdb_index = rs.getString("imdb_index");
                 Integer imdb_id = rs.getInt("imdb_id");
                 String name_pcode_nf = rs.getString("name_pcode_nf");
                 String surname_pcode = rs.getString("surname_pcode");
                 String md5sum = rs.getString("md5sum");
                 
                 row.put("id", id);
                 row.put("name", name);
                 row.put("imdb_index", imdb_index);
                 row.put("imdb_id", imdb_id);
                 row.put("name_pcode_nf", name_pcode_nf);
                 row.put("surname_pcode", surname_pcode);
                 row.put("md5sum", md5sum);
                
                 
                 for(Entry<String, Object> cell : row.entrySet())
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void company_name_getData() {
        String selectTableSQL =  
                "SELECT id, name, country_code, imdb_id, name_pcode_nf, "
                + "name_pcode_sf, md5sum from company_name ";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 company_name_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 String name = rs.getString("name");
                 String country_code = rs.getString("country_code");
                 Integer imdb_id = rs.getInt("imdb_id");
                 String name_pcode_nf = rs.getString("name_pcode_nf");
                 String name_pcode_sf = rs.getString("name_pcode_sf");
                 String md5sum = rs.getString("md5sum");
                 
                 row.put("id", id);
                 row.put("name", name);
                 row.put("country_code", country_code);
                 row.put("imdb_id", imdb_id);
                 row.put("name_pcode_nf", name_pcode_nf);
                 row.put("name_pcode_sf", name_pcode_sf);
                 row.put("md5sum", md5sum);
                
                 
                 for(Entry<String, Object> cell : row.entrySet())
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void company_type_getData() {
        String selectTableSQL =  
                "SELECT id, kind from company_type ";
        String insertTableSQL="";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 company_type_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 String kind = rs.getString("kind");
                 
                 row.put("id", id);
                 row.put("kind", kind);
                 
                 insertTableSQL = insertTableSQL.concat("INSERT INTO company_type (id, kind) VALUES ('"+rs.getInt("id")+"', '"+rs.getString("kind")+"')"+"\n");
                 
                 
                 
                  
                
                 
                /* for(Entry<String, Object> cell : row.entrySet())
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());*/
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
        
        System.out.println(insertTableSQL);
        
        try(Statement stmt = dbConPostgres.getConnection().createStatement();){
                stmt.addBatch(insertTableSQL); 
                stmt.executeBatch();
        }catch(SQLException ex){
        ex.printStackTrace();
        }
    }
    
  

    private void info_type_getData() {
        String selectTableSQL =  
                "SELECT id, info from info_type ";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 info_type_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 String info = rs.getString("info");
                 
                 row.put("id", id);
                 row.put("info", info);
                
                 
                 for(Entry<String, Object> cell : row.entrySet())
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void kind_type_getData() {
        String selectTableSQL =  
                "SELECT id, kind from kind_type s";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 kind_type_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 String kind = rs.getString("kind");
                 
                 row.put("id", id);
                 row.put("kind", kind);
                
                 
                 for(Entry<String, Object> cell : row.entrySet())
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void link_type_getData() {
        String selectTableSQL =  
                "SELECT id, link from link_type ";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 link_type_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 String link = rs.getString("link");
                 
                 row.put("id", id);
                 row.put("link", link);
                
                 
                 for(Entry<String, Object> cell : row.entrySet())
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void movie_companies_getData() {
        int index = 1;
        
        String selectTableSQL =  
                "SELECT id, movie_id, company_id, "
                + "company_type_id, note from movie_companies ";
        
        
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 movie_companies_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 Integer movie_id = rs.getInt("movie_id");
                 Integer company_id = rs.getInt("company_id");
                 Integer company_type_id = rs.getInt("company_type_id");
                 String note = rs.getString("note");
                 
                 row.put("id", id);
                 row.put("movie_id", movie_id);
                 row.put("company_id", company_id);
                 row.put("company_type_id", company_type_id);
                 row.put("note", note);
                
                 
                 for(Entry<String, Object> cell : row.entrySet()){
                     System.out.println("--------------------"+index+"------------------------------");
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
                 }
                 index++;
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void movie_info_getData() {
        int index = 1;
        String selectTableSQL =  
                "SELECT id, movie_id, info_type_id, info, note from movie_info ";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 movie_info_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 Integer movie_id = rs.getInt("movie_id");
                 Integer info_type_id = rs.getInt("info_type_id");
                 String info = rs.getString("info");
                 String note = rs.getString("note");
                 
                 row.put("id", id);
                 row.put("movie_id", movie_id);
                 row.put("info_type_id", info_type_id);
                 row.put("info", info);
                 row.put("note", note);
                
                 
                 for(Entry<String, Object> cell : row.entrySet()){
                     System.out.println("--------------------"+index+"------------------------------");
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
                 }
                 index++;
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void movie_info_idx_getData() {
        int index = 1;
        String selectTableSQL =  
                "SELECT id, movie_id, info_type_id, info, "
                + "note from movie_info_idx ";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 movie_info_idx_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 Integer movie_id = rs.getInt("movie_id");
                 Integer info_type_id = rs.getInt("info_type_id");
                 String info = rs.getString("info");
                 String note = rs.getString("note");
                 
                 row.put("id", id);
                 row.put("movie_id", movie_id);
                 row.put("info_type_id", info_type_id);
                 row.put("info", info);
                 row.put("note", note);
                
                
                 
                 for(Entry<String, Object> cell : row.entrySet()){
                     System.out.println("--------------------"+index+"------------------------------");
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
                 }
                 index++;
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void movie_link_getData() {
        String selectTableSQL =  
                "SELECT id, movie_id, linked_movie_id, link_type_id from movie_link ";
       try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
            while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 movie_link_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 Integer movie_id = rs.getInt("movie_id");
                 Integer linked_movie_id = rs.getInt("linked_movie_id");
                 Integer link_type_id = rs.getInt("link_type_id");
                 
                 row.put("id", id);
                 row.put("movie_id", movie_id);
                 row.put("linked_movie_id", linked_movie_id);
                 row.put("link_type_id", link_type_id);
                
                 
                 for(Entry<String, Object> cell : row.entrySet())
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void name_getData() {
        String selectTableSQL =  
                "SELECT id, name, imdb_index, imdb_id, gender, "
                + "name_pcode_cf, name_pcode_nf, surname_pcode, md5sum from name";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 name_table.add(row);
                 
                 Integer id = rs.getInt("id");
                 String name = rs.getString("name");
                 String imdb_index = rs.getString("imdb_index");
                 Integer imdb_id = rs.getInt("imdb_id");
                 String gender = rs.getString("gender");
                 String name_pcode_cf = rs.getString("name_pcode_cf");
                 String name_pcode_nf = rs.getString("name_pcode_nf");
                 String surname_pcode = rs.getString("surname_pcode");
                 String md5sum = rs.getString("md5sum");
                 
                 row.put("id", id);
                 row.put("name", name);
                 row.put("imdb_index", imdb_index);
                 row.put("imdb_id", imdb_id);
                 row.put("gender", gender);
                 row.put("name_pcode_cf", name_pcode_cf);
                 row.put("name_pcode_nf", name_pcode_nf);
                 row.put("surname_pcode", surname_pcode);
                 row.put("md5sum", md5sum);
                
                 
                 for(Entry<String, Object> cell : row.entrySet())
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void person_info_getData() {
        int index=0;
        String selectTableSQL =  
                "SELECT id, person_id, info_type_id, info, note from person_info ";
       try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 person_info_table.add(row);
                
                 Integer id = rs.getInt("id");
                 Integer person_id = rs.getInt("person_id");
                 Integer info_type_id = rs.getInt("info_type_id");
                 String info = rs.getString("info");
                 String note = rs.getString("note");
                 
                 row.put("id", id);
                 row.put("person_id", person_id);
                 row.put("info_type_id", info_type_id);
                 row.put("info", info);
                 row.put("note", note);
                
                 
                 for(Entry<String, Object> cell : row.entrySet()){
                     System.out.println("--------------------"+index+"------------------------------");
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
                 }
                 index++;
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }

    private void role_type_getData() {
        String selectTableSQL =  
                "SELECT id, role from role_type ";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 role_type_table.add(row);
                 Integer id = rs.getInt("id");
                 String role = rs.getString("role");
                 
                 row.put("id", id);
                 row.put("role", role);
                
                 
                 for(Entry<String, Object> cell : row.entrySet())
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }
    
    private void title_getData() {
        int index=1;
        String selectTableSQL =  
                "SELECT id, title, imdb_index, kind_id, production_year, "
                + "imdb_id, phonetic_code, episode_of_id, season_nr, "
                + "episode_nr, series_years, md5sum from title ";
        try(Statement stmt = dbConPostgres.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(selectTableSQL);)
        { 
             
             while(rs.next()){
                 Map<String,Object> row = new HashMap<String, Object>();
                 title_table.add(row);
                 Integer id = rs.getInt("id");
                 String title = rs.getString("title");
                 String imdb_index = rs.getString("imdb_index");
                 Integer kind_id = rs.getInt("kind_id");
                 Integer production_year = rs.getInt("production_year");
                 Integer imdb_id = rs.getInt("imdb_id");
                 String phonetic_code = rs.getString("phonetic_code");
                 Integer episode_of_id = rs.getInt("episode_of_id");
                 Integer season_nr = rs.getInt("season_nr");
                 Integer episode_nr = rs.getInt("episode_nr");
                 String series_years = rs.getString("series_years");
                 String md5sum = rs.getString("md5sum");
                 row.put("id", id);
                 row.put("title", title);
                 row.put("imdb_index", imdb_index);
                 row.put("kind_id", kind_id);
                 row.put("production_year", production_year);
                 row.put("imdb_id", imdb_id);
                 row.put("phonetic_code", phonetic_code);
                 row.put("episode_of_id", episode_of_id);
                 row.put("season_nr", season_nr);
                 row.put("episode_nr", episode_nr);
                 row.put("series_years", series_years);
                 row.put("md5sum", md5sum);
                 
                 for(Entry<String, Object> cell : row.entrySet()){
                     System.out.println("--------------------"+index+"------------------------------");
                     System.out.println("Title: "+cell.getKey()+"\tWert: "+cell.getValue());
                 }
                 index++;
             }
             
         }catch(SQLException ex){
                ex.printStackTrace();
        }
    }
    
}
