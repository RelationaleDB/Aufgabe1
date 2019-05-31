/*
 * Copyright 2014 Rainer Stoermer
 */
package rdb.app;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rdb.bdo.OracleUser;
import rdb.bdo.PostgresUser;
import rdb.data.DbConnection;
import rdb.data.DbConnectionSingletonFactory;
import rdb.RDBSchemaMigration;

/**
 * Realizes the login-service in the application logic-layer.
 * manages login and logout for both postgres and oracle 
 * using the appropriate classes of the data-layer.
 * 
 * @author Rainer Stoermer
 */
public class LoginAL {

    private DbConnectionSingletonFactory dbConFactory;
    private DbConnection dbConPostgres;
    private DbConnection dbConOracle;

    /**
     * Performs login to PostgreSQL-DBMS using data layer and singleton-BDOs PostgresUser and DatabaseData
     * 
     * @return true if login successful, false if not
     */
    public boolean postgresUserLogin() {
        // use the SingletonFactory to get the one and only DbConnection-Instance
        dbConFactory = DbConnectionSingletonFactory.getDbConnectionSingletonFactory();
        dbConPostgres = dbConFactory.getDbConnection("postgres");
        dbConPostgres.openConnection();  
        
        
        try {
            // check success of openConnection -> connection must be valid...
            if (dbConPostgres.getConnection().isValid(0)) {
                Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.INFO, "Postgres-Login successful for user {0}",
                        PostgresUser.getInstance().getUsername());
                return true;
            } else {
                Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.SEVERE, "Postgres-Login failed for user {0}", 
                        PostgresUser.getInstance().getUsername());
                return false;
            }
        } catch (NullPointerException | SQLException e) {
            Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.SEVERE, "Error opening postgres connection, "
                    + "connection invalid: " + e.getMessage());
        }
        return false;
    }

    /**
     * logout the postgres-user (close postgres-connection and clear user/password for postgres).
     */
    public void postgresUserLogout() {
        // use SingletonFactory to get appropriate DbConnection...
        dbConFactory = DbConnectionSingletonFactory.getDbConnectionSingletonFactory();
        dbConPostgres = dbConFactory.getDbConnection("postgres");

        try {
            if (!dbConPostgres.getConnection().isClosed() && dbConPostgres.getConnection().isValid(0)) {
                dbConPostgres.closeConnection();
            }
            Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.INFO,
                    "Postgres-Logout successful for user {0}", PostgresUser.getInstance().getUsername());
            PostgresUser.getInstance().clearUser();
        } catch (NullPointerException | SQLException e) {
            Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.SEVERE,
                    "Error closing postgres connection," + " connection invalid: " + e.getMessage());
        }
    }

    /**
     * Performs login to Oracle-DBMS using data layer and singleton-BDOs OracleUser and DatabaseData
     * 
     * @return true if login successful, false if not
     */
    public boolean oracleUserLogin() {
        // use the SingletonFactory to get the one and only DbConnection-Instance
        dbConFactory = DbConnectionSingletonFactory.getDbConnectionSingletonFactory();
        dbConOracle = dbConFactory.getDbConnection("oracle");
        dbConOracle.openConnection();

        try {
            // check success of openConnection -> connection must be valid...
            if (!dbConOracle.getConnection().isClosed() && dbConOracle.getConnection().isValid(0)) {
                Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.INFO, "Oracle-Login successful for user {0}", OracleUser.getInstance().getUsername());
                return true;
            } else {
                Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.SEVERE, "Oracle-Login failed for user {0}", OracleUser.getInstance().getUsername());
                return false;
            }
        } catch (NullPointerException | SQLException ex) {
            Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.SEVERE, "Error opening oracle connection, connection invalid: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Logout the oracle-User (close connection and clear oracle-user/password).
     */
    public void oracleUserLogout() {
        // use SingletonFactory to get appropriate DbConnection...
        dbConFactory = DbConnectionSingletonFactory.getDbConnectionSingletonFactory();
        dbConOracle = dbConFactory.getDbConnection("oracle");

        try {
            if (dbConOracle.getConnection().isValid(0)) {
                dbConOracle.closeConnection();
            }
            Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.INFO, "Oracle-Logout successful for user {0}", OracleUser.getInstance().getUsername());
            OracleUser.getInstance().clearUser();
        } catch (NullPointerException | SQLException ex) {
            Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.SEVERE, "Error closing oracle connection, connection invalid: " + ex.getMessage());
        }
    }
}
