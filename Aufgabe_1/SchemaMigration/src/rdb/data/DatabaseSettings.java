/*
 *  Copyright 2014 Rainer Stoermer
 */
package rdb.data;

/**
 * Simple BDO to change server-data without touching the data-layer-classes.
 *
 * @author Rainer Stoermer
 */
public class DatabaseSettings {

    /**
     * final for Oracle-Server-JDBC-String
     */
    public static final String ORACLE_DATABASE_URL = "jdbc:oracle:thin:@dbvm02.iai.uni-bonn.de:1521:dbvm02";
    //public static final String ORACLE_DATABASE_URL = "jdbc:oracle:thin:@localhost:1521/ORCLCDB.localdomain";

    /**
     * final for Oracle-schemaname
     * Be careful, ORACLE_SCHEMA is always upper case!
     */
    public static final String ORACLE_SCHEMA = ""; //enter your username here
    
    /**
     * final for PostgreSQL-Server-JDBC-String
     */
    public static final String POSTGRES_DATABASE_URL = "jdbc:postgresql://dbvm09.iai.uni-bonn.de:5432/imdb";
    //public static final String POSTGRES_DATABASE_URL = "jdbc:postgresql://localhost:5432/imdb";

    /**
     * final for PostgreSQL-schemaname
     */
    public static final String POSTGRES_SCHEMA = "";
}
