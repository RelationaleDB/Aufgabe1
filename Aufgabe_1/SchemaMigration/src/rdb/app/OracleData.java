package rdb.app;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.util.Pair;
import rdb.data.DbConnection;
import rdb.data.DbConnectionPostgres;
import rdb.data.DbConnectionSingletonFactory;

/**
 * Diese Klasse übernimmt erstellt eine Oracle Datenbank und übernimmt die Aufgabe vom Transfer der Daten 
 * @author Vanessa Paluch, Atilla Demir
 */
public class OracleData {
    
    private DbConnectionSingletonFactory dbConFactory;
    private DbConnection dbConOracle;
    private DbConnection dbConPostgres;
    private DatabaseMetaData databaseMetaDataPG=null;
    private DatabaseMetaData databaseMetaDataORCL=null;
    
    
    /**
     * Diese beiden String Variablen dienen nur zur Übersichtlichkeit in Strings, 
     * die für sql Statements vorbereitet werden
     */
    String nextLine="\n";
    String tab="\t";
    
    /**
     * Diese Variable unterdrückt verschiedene Ausgaben von einigen Methoden im Output Fenster, wenn sie [false] gesetzt wird 
     */
    boolean message_MetaData_Created=false;
    
    /**
     * In dieser Variable werden die sql Statements gespeichert
     */
    StringBuilder[] sql;
    
    /**
     * Diese Variable verwendet die Methode messageTransferFinished() um die Anzahl der Aufrufe anzuzeigen
     */
    int count = 1;
    
    /**
     * In diesen ArrayList's aus Strings werden die sql Statements gespeichert (gesammelt)
     */
    ArrayList<String> sql_PK = null;
    ArrayList<String> sql_FK = null;
    
    /**
     * In diesen ArrayList's aus Strings werden die Tabellen Informationen gespeichert (gesammelt)
     */
    ArrayList<String> tableNamesList_Oracle=null;
    ArrayList<String> tableNamesList_Postgres=null;
    ArrayList<Pair<String, String>> foreignKeysList_Oracle=null;
    
    List<Map<String, ArrayList<String>>> table_Column_Names = new ArrayList<Map<String,ArrayList<String>>>();
    
    /**
     * Für jede Tabelle eine ArrayList bestehend aus Paaren von Strings und Object's um die einzelnen Zeilen Informationrn abzuspeichern 
     */
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
    
    
    /**
     * Variablen für die Beschränkungen (Vor dem Programm Start setzen!)
     */
    int productionYear_A = 1910;
    int productionYear_B = 1910;
    
    boolean setFilter_Cast_info_Role_type_ = true;
    String cast_info_Role_type1 = "actor";
    String cast_info_Role_type2 = "producer";
    
    boolean setFilter_Movie_info_Info_type = true;
    String movie_info_Info_type1 = "runtimes";
    String movie_info_Info_type2 = "genres";
    
    boolean setFilter_Movieinfo_idx_Info_type = true;
    String movie_info_idx_Info_type1 = "votes distribution";
    String movie_info_idx_Info_type2 = "rating";
    
    boolean setFilter_Movie_companies_Company_type = true;
    String movie_companies_Company_type1 = "distributors";
    String movie_companies_Company_type2 = "special effects companies";
    
    boolean setFilter_Person_info_Info_type = true;
    String person_info_Info_type1 = "height";
    String person_info_Info_type2 = "spouse";
    
    boolean setFilter_ProductionYear = true;
    boolean setFilter_Title_kind_Type = true;
    String title_kind_Type1 = "movie";
    String title_kind_Type2 = "video game";
    
    /**
     * Zum Starten des Transfers aus der Hauptklasse im Maven Projekt. !!
     */
    public void initialize(){
        dbConFactory = DbConnectionSingletonFactory.getDbConnectionSingletonFactory();
        dbConOracle = dbConFactory.getDbConnection("oracle");
        dbConPostgres = dbConFactory.getDbConnection("postgres");
        
        /**
         * Verbindung zur Datenbank, um Metadaten abfragen für die einzelnen Methoden zu ermöglichen
         */
        getMetaDataConnection();
        
        
       /** Einzelne Methoden (auskommetiert) werden nacheinander aufgerufen: Löschen vom Oracle Schema
        * Diese Methoden nur verwenden, um vorhandenes Schema zu löschen !! 
        * siehe dazu auch auskommetierte Zeilen in der Methode oracle_getTableNames() !!
        */
        //oracle_getTableNames();
        //oracle_getConstraints();
        //oracle_Drop_Constraints();
        //oracle_Drop_Tables();
        
        
        
        /**
         * Einzelne Methoden werden nacheinander aufgerufen: Speichern von Postgres MetaData Informationen
         */
        //postgres_getTableNames();
        //postgres_getColumnNames();
        //postgres_getPrimaryKeys();
        //postgres_getForeignKeys();
      
        
        
        /**
         * Einzelne Methoden nacheinander aufgerufen: Erstellen von einem Oracle Schema
         * Davon ausgenommen die oracle_createForeignKeys() (auskommetiert), die erst zum Schluß aufgerufen werden soll,
         * um den DatenTransfer in die Oracle Datenbank zu erleichtern
         */
        //oracle_createTables();  
        //oracle_createPrimaryKeys();
        //oracle_createForeignKeys();
        
        
        
        /**
         * Einzelne Methoden werden zusammengefasst in einer Methode nacheinander aufgerufen: 
         * Tabelleninhalte werden zur Oracle DB übertragen (DatenTransfer)
         */
        getPostgresRows_And_writeOracleRows();
        
        
        
        /**
         * Diese Method wird erst nach der getPostgresRows_And_writeOracleRows() - Methode aufgerufen, 
         * um den DatenTransfer zu erleichtern
         * Leider tritt hierbei ein Fehler auf, der nicht auftritt wenn man diese Methode 
         * vor dem DatenTransfer aufruft(?) --> Deshalb auskommentiert !!
         */
        //oracle_createForeignKeys();
    }
    
    
    /**
     * Einzelne Methoden werden in einer Methoden nacheinander aufgerufen: 
     * Tabelleninhalte werden zur Oracle DB übertragen
     * Weitere Einzelheiten zu den Methoden siehe Java Dokumentation
     */
    public void getPostgresRows_And_writeOracleRows(){
        //getRows("cast_info",10,true);
        /*get_Cast_Info_And_writeToOracleDB(10, true, true);
       get_Char_Name__And_writeToOracleDB(100, true, true);
       get_Company_Type__And_writeToOracleDB(0, true, true);
       get_Company_Name__And_writeToOracleDB(100, true, true);
       get_Info_Type__And_writeToOracleDB(0, true, true);
       get_Kind_Type__And_writeToOracleDB(0, true, true);
       get_Link_Type__And_writeToOracleDB(0, true, true);
       get_Movie_Companies_And_writeToOracleDB(100, true, true);
       get_Movie_Info_And_writeToOracleDB(100, true, true);
       get_Movie_Info_Idx__And_writeToOracleDB(100, true, true);
       get_Movie_Link_And_writeToOracleDB(100, true, true);
       get_Name_Table__And_writeToOracleDB(100,true, true);
       get_Person_Info__And_writeToOracleDB(100, true, true); 
       get_Role_Type__And_writeToOracleDB(0, true, true);
       get_Title_And_writeToOracleDB(1000, true, true);*/
       
    }
    
    /**
     * Nach dem übertragen der Daten rufen die einzelnen Methoden diese auf um über den Abschluss zu informieren
     * @param s Name der Tabelle
     */
    public void messageTransferFinished(String s){
        System.out.println("*******************************"+(count++)+"*********************************************************");
        System.out.println("Der Transfer von der Tabelle: "+s+" zur Oracle Datenbank wurde abgeschlossen.");
        System.out.println("******************************************************************************************\n\n");
    }

    /**
     * Verbindung zu den beiden DB um auf Metadaten zugreifen zu können
     */
    public void getMetaDataConnection(){
        try {
            databaseMetaDataORCL = dbConOracle.getConnection().getMetaData();
        } catch (SQLException e) {
            System.err.println("Die Verbindung zur Oracle Datenbank (DatabaseSchema) war nicht erfolgreich.");
            e.printStackTrace();
        }
        try {
            databaseMetaDataPG = dbConPostgres.getConnection().getMetaData();
        } catch (SQLException e) {
            System.err.println("Die Verbindung zur Postgres Datenbank (DatabaseSchema) war nicht erfolgreich.");
            e.printStackTrace();
        }
    }
    
    /**
     * Oracle Tabellen Namen auslesen und in der ArrayList aus Strings speichern
     */
    public void oracle_getTableNames(){
       String tableName= "";
       tableNamesList_Oracle = new ArrayList<String>();
        

        try(ResultSet rs = databaseMetaDataORCL.getTables(null, null, null, new String[]{"TABLE"});)
        {
            
            while(rs.next())
            {
                if(rs.getString("TABLE_SCHEM").equals("DBUSER")){
                    tableName = rs.getString("TABLE_NAME");
                    tableNamesList_Oracle.add(tableName);
                    if(message_MetaData_Created)
                        System.out.println("Tabelle gefunden, Name: "+tableName);
                }
            }
        }catch(SQLException ex){
        ex.printStackTrace();
        }
        if(tableNamesList_Oracle.isEmpty()) 
            System.err.println("Es sind keine Tabellen vorhanden.");
    }
    
    /**
     * Oracle Constraints (Fremdschlüssel-Info) auslesen und in der ArrayList aus String Paaren speichern
     */
    public void oracle_getConstraints(){
       foreignKeysList_Oracle = new ArrayList<Pair<String,String>>();
        
        for(String table : tableNamesList_Oracle){
            try(ResultSet rs = databaseMetaDataORCL.getImportedKeys( "" , "" , table);)
            {
                    while(rs.next())
                    {
                        Pair <String,String> pair = new Pair(rs.getString("FKTABLE_NAME"),rs.getString("FK_NAME"));
                        foreignKeysList_Oracle.add(pair);
                        if(message_MetaData_Created)
                            System.out.println("FK-Info gelesen: "+"ALTER TABLE "+pair.getKey()+" DROP CONSTRAINT "+"\""
                                        +pair.getValue()+"\"");
                    }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        if(tableNamesList_Oracle.isEmpty()) 
            System.err.println("Es sind keine Fremdschlüssel vorhanden.");
    }
 
    /**
     * Die in der Methode "oracle_getConstraints()"-Methode gelesenen Daten werden hier verwendet, um die Fremschlüssel zu löschen 
     */
    public void oracle_Drop_Constraints(){
        
        if(!tableNamesList_Oracle.isEmpty()&&!foreignKeysList_Oracle.isEmpty()){
            try(Statement stmt = dbConOracle.getConnection().createStatement();)
            {
                for(Pair pair : foreignKeysList_Oracle){
                    stmt.executeUpdate("ALTER TABLE "+pair.getKey()+" DROP CONSTRAINT "+"\""
                                    +pair.getValue()+"\"");
                    if(message_MetaData_Created)
                        System.out.println("FK-Info gesetzt: "+"ALTER TABLE "+pair.getKey()+" DROP CONSTRAINT "+"\""
                                    +pair.getValue()+"\"");
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.err.println("Es sind keine Fremdschlüssel zum löschen vorhanden.");
        }
    }
    
    /**
     * Die in der Methode: oracle_getTableNames() gelesenen Daten werden hier verwendet, um die Tabellen zu löschen
     */
    public void oracle_Drop_Tables(){
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
    
    /**
     * Postgres Tabellen Namen auslesen und in der ArrayList aus Strings speichern
     */
    public void postgres_getTableNames(){
        tableNamesList_Postgres = new ArrayList<String>();
        

        //Retrieve TABLES "names"
        try{
            ResultSet rs = databaseMetaDataPG.getTables(null, null, null, new String[]{"TABLE"});
            while(rs.next())
            {
                tableNamesList_Postgres.add(rs.getString("TABLE_NAME"));
                if(message_MetaData_Created)
                    System.out.println(rs.getString("TABLE_NAME"));
            }
        }catch(SQLException ex){
        ex.printStackTrace();
        }
    }
    
    /**
     * 1)Postgres Attribut-Namen (Spalten-Namen) auslesen und in einer Map speichern, um diese dann wiederum einzeln in 
     * einer Liste zu speichen
     * 2)Daneben werden aus dem Resultset erhaltenen Attribut Informationen in einzelne Create Statements 
     * zum Erstellen von Tabellen verwendet
     * 3)Diese Create Statements werden in die Variable sql (vom StringBuilder Typ) gepeichert
     */
    public void postgres_getColumnNames(){
        if(tableNamesList_Postgres!=null){
            sql = new StringBuilder[tableNamesList_Postgres.size()];
            String tableNameStr = "";
            int index=0;
            //Retrieve TABLES "names"
            try{
                for(String tableName : tableNamesList_Postgres){
                    ArrayList<String> columnNamesList = new ArrayList<>();
                    tableNameStr=tableName.toString();
                    sql[index] = new StringBuilder("");
                    sql[index].append("CREATE TABLE "+tableName.toString()+" ( "+nextLine);
                    ResultSet rs = databaseMetaDataPG.getColumns(null,null, tableName.toString(), null);
                    while(rs.next()){
                        sql[index].append(rs.getString("COLUMN_NAME")+tab+convertType_Postgres_Oracle(rs.getString("TYPE_NAME"),rs.getInt("COLUMN_SIZE")));
                        sql[index].append(",");
                        sql[index].append(nextLine);
                        columnNamesList.add(rs.getString("COLUMN_NAME"));
                    }
                    sql[index].deleteCharAt(sql[index].lastIndexOf(nextLine));
                    sql[index].deleteCharAt(sql[index].lastIndexOf(","));
                    sql[index].append(")");
                    index++;
                    Map<String, ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
                    map.put(tableNameStr, columnNamesList);
                    table_Column_Names.add(map);
                }
            }catch(SQLException ex){
            ex.printStackTrace();
            }
            
        }else{
            System.out.println("Es wurde keine TabellenNamen Liste erstellt oder diese ist leer!");
        }
    }
    
    /**
     * Postgres DB Attribut_typen in Oracle DB Attribut_typen konvertieren
     * @param s Postgres DB Attribut_typ als String erhalten
     * @param size Die Größe des Attributs
     * @return 
     */
    public String convertType_Postgres_Oracle(String s, int size){
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
    
    /**
     * In einer ArrayList aus Strings werden die einzelnen sql Befehle zum erstellen von
     * Primärschlüsseln abgespeichert:
     * Diese Informationen werden vom ResultSet getPrimaryKeys geliefert
     */
    public void postgres_getPrimaryKeys(){
       sql_PK = new ArrayList<String>();
        try {
            databaseMetaDataPG = dbConPostgres.getConnection().getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       //Retrieve Keys
        try{
            for(String table : tableNamesList_Postgres){
                ResultSet rs = databaseMetaDataPG.getPrimaryKeys( "" , "" , table);
                while(rs.next())
                {
                    sql_PK.add("ALTER TABLE "+rs.getString("TABLE_NAME")
                        +" ADD CONSTRAINT "+rs.getString("PK_NAME")+" PRIMARY KEY ("
                        +rs.getString("COLUMN_NAME")+" )"+nextLine);
                }
            }
        }catch(SQLException ex){
        ex.printStackTrace();
        }
        
     }
    
    /**
     * In einer ArrayList aus Strings werden die einzelnen sql Befehle zum erstellen von
     * Fremdschlüsseln abgespeichert:
     * Diese Informationen werden vom ResultSet getImportedKeys geliefert
     */
    public void postgres_getForeignKeys(){
       sql_FK = new ArrayList<String>();

        //Retrieve Keys
        try{
            for(String table : tableNamesList_Postgres){
                ResultSet rs = databaseMetaDataPG.getImportedKeys( "" , "" , table);
                while(rs.next())
                {
                    sql_FK.add("ALTER TABLE "+rs.getString("FKTABLE_NAME")
                        +" ADD FOREIGN KEY ( "+rs.getString("FKCOLUMN_NAME")+" ) "
                        +" REFERENCES "+rs.getString("PKTABLE_NAME")
                        +" ( "+rs.getString("PKCOLUMN_NAME")+" )");
                }
            }
        }catch(SQLException ex){
        ex.printStackTrace();
        }   
    }
    
    /**
     * Die in der Methode postgres_getColumnNames() in die sql Variable gespeicherten 
     * sql Statements werden hier weiterverwendet
     * Diese einezelnen sql Statements werden in einer Batch Datei gespeichert und
     * dann gemeinsam zur Datenbank gesendet, um schließlich Tabellen mit dazugehörigen Attributen zu erstellen
     */
    public void oracle_createTables(){
        if(sql!=null){
            try(Statement stmt = dbConOracle.getConnection().createStatement();)
            {
                if(message_MetaData_Created)
                    System.out.println("Folgende Tabellen wurden erstellt: ");
                for (int i = 0; i < sql.length; i++) {
                    stmt.addBatch(sql[i].toString());
                    if(message_MetaData_Created)
                        System.out.println(tableNamesList_Postgres.get(i));
                }
                stmt.executeBatch();
            }catch(SQLException ex){
                    ex.printStackTrace();
            }
        }else{
            System.err.println("Es sind keine SQL Statements zum erstellen von Tabllen vorhanden");
        }
    }
    
    
    
    /**
     * Die in der Methode postgres_getPrimaryKeys() in die sql_PK Variable gespeicherten 
     * sql Statements werden hier weiterverwendet
     * Diese einezelnen sql Statements werden in einer Batch Datei gespeichert und
     * dann gemeinsam zur Datenbank gesendet, um schließlich Primäschlüssel zu erstellen 
     */
    public void oracle_createPrimaryKeys(){
        if(sql!=null&&sql_PK!=null&&!sql_PK.isEmpty()){
            try(Statement stmt = dbConOracle.getConnection().createStatement();)
            {
                for(String s : sql_PK){
                   stmt.addBatch(s);
                }
                stmt.executeBatch();
            }catch(SQLException ex){
                    ex.printStackTrace();
            }
            if(message_MetaData_Created){
                System.out.println("Folgende Primärschlüssel wurden erstellt: ");
                for(String s : sql_PK){
                    System.out.println(s);
                }
            }
        }else{
            System.err.println("Es sind keine SQL Statements zum erstellen von Tabellen/Primärschlüsseln vorhanden");
        }    
    }
    
    /**
     * Die in der Methode postgres_getForeignKeys() in die sql_FK Variable gespeicherten 
     * sql Statements werden hier weiterverwendet
     * Diese einezelnen sql Statements werden in einer Batch Datei gespeichert und
     * dann gemeinsam zur Datenbank gesendet, um schließlich Fremdschlüssel zu erstellen 
     */
    public void oracle_createForeignKeys(){
        if(sql!=null&&sql_FK!=null&&!sql_FK.isEmpty()){
            try(Statement stmt = dbConOracle.getConnection().createStatement();)
            {
                for(String s : sql_FK){
                    stmt.addBatch(s); 
                }
                stmt.executeBatch();
            }catch(SQLException ex){
                    ex.printStackTrace();
            }
            if(message_MetaData_Created){
                System.out.println("Folgende Fremdschlüssel wurden erstellt: ");
                for(String s : sql_FK){
                    System.out.println(s);
                }
            }
        }else{
            System.err.println("Es sind keine SQL Statements zum erstellen von Tabellen/Fremdschlüsseln vorhanden");
        } 
    }
    
    public ArrayList<Map<String, ArrayList<String>>> getTablesAndColumnsByPostgres(){
        ArrayList<Map<String, ArrayList<String>>> tablesColumns = new ArrayList<Map<String, ArrayList<String>>>();
        ResultSet rs = null;
        ResultSet rs2 = null;
        try{
            Map<String, ArrayList<String>> map = new HashMap<>();
            rs = databaseMetaDataPG.getTables(null, null, null, new String[]{"TABLE"});
            while(rs.next()){
                ArrayList<String> arraylist = new ArrayList<>();
                rs2 = databaseMetaDataPG.getColumns(null,null, rs.getString("TABLE_NAME"), null);
                while(rs2.next()){
                    arraylist.add(rs.getString("COLUMN_NAME"));
                }
                map.put(rs.getString("TABLE_NAME"), arraylist);
                tablesColumns.add(map);
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return tablesColumns;
    }
    
    public void tables(String tablename, int limit_rows, boolean delete_before_insert, String[] types){
        if(tableName_exists(tablename)){
            ResultSet rs = null;
            ResultSetMetaData rsMD= null;
            PreparedStatement ps_INSERT = null;
            PreparedStatement ps_DELETE= null;

            String selectTableSQL ="SELECT * from "+tablename+" ";

            if(setFilter_Cast_info_Role_type_)        
                selectTableSQL =  selectTableSQL.concat("WHERE person_role_id IN(SELECT id FROM role_type WHERE role = '"+cast_info_Role_type1+"' OR role = '"+cast_info_Role_type1+"')");
            if(limit_rows>0)
                selectTableSQL =  selectTableSQL.concat(tab+"FETCH FIRST "+limit_rows+" ROWS ONLY");

            try
            {
                rs  = dbConPostgres.getConnection().createStatement().executeQuery(selectTableSQL);
                rsMD = rs.getMetaData();
                ps_INSERT = dbConOracle.getConnection().prepareStatement(getInsertStatement_FromResultMetaData(rsMD).toString());
                ps_DELETE = dbConOracle.getConnection().prepareStatement("DELETE FROM "+tablename+" WHERE id=?");
                int columncount = rsMD.getColumnCount();

                while(rs.next()){
                    if(delete_before_insert){
                        ps_DELETE.setInt(1, rs.getInt(1));
                        ps_DELETE.addBatch();
                    }
                    for (int i = 1; i <= columncount; i++) {
                        ps_INSERT.setObject(i, rs.getObject(i));
                    }
                    ps_INSERT.addBatch();
                }
                if(delete_before_insert){
                    ps_DELETE.executeBatch();
                }
                ps_INSERT.executeBatch();

            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }
    
    public StringBuilder getInsertStatement_FromResultMetaData(ResultSetMetaData rsMD){
        StringBuilder sql_INSERT=null;
        StringBuilder sql_INSERT_tmp=null;
        
        try
        {
            int columncount = rsMD.getColumnCount();
            sql_INSERT = new StringBuilder("INSERT INTO "+rsMD.getTableName(1)+" (");
            sql_INSERT_tmp = new StringBuilder(") VALUES (");
            for (int i = 1; i <= columncount; i++) {
                sql_INSERT.append(" "+rsMD.getColumnName(i)+",");
                sql_INSERT_tmp.append("?, ");
        }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        sql_INSERT.deleteCharAt(sql_INSERT.lastIndexOf(","));
        sql_INSERT_tmp.deleteCharAt(sql_INSERT_tmp.lastIndexOf(","));
        sql_INSERT_tmp.append(")");
        sql_INSERT.append(sql_INSERT_tmp.toString());
        return sql_INSERT;
    }
    
    public boolean tableName_exists(String tablename){
        boolean table_exists = false;
        try{
            ResultSet rs = databaseMetaDataPG.getTables(null, null, null, new String[]{"TABLE"});
            while(rs.next())
            {
                if(rs.getString("TABLE_NAME").equals(tablename));
                    table_exists=true;
            }
        }catch(SQLException ex){
        ex.printStackTrace();
        }
        return table_exists;
    }
  
}//Ende der Klasse OracleData
