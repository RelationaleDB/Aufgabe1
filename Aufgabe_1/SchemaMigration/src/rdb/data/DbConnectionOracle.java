/*
 * Copyright 2014 Rainer Stoermer
 */
package rdb.data;

import rdb.bdo.OracleUser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class to establish a connection to oracle.
 *
 *
 * @author Rainer Stoermer
 */
public class DbConnectionOracle implements rdb.data.DbConnection {

    private final String DB_DRIVER = "oracle.jdbc.OracleDriver";
    private Connection con;

    /**
     * constructor
     */
    protected DbConnectionOracle() {
    }

    /**
     *
     * @return the oracle sql.connection
     */
    @Override
    public Connection getConnection() {
        return con;
    }

    /**
     * Opens the Oracle-sql.connection, using OracleUser-Data and DatabaseSettings for connecting.
     *
     * @return the open sql.connection for the Oracle-DBMS
     */
    @Override
    public Connection openConnection() {
        OracleUser user = OracleUser.getInstance();
        return openConnection(user.getUsername(),user.getPassword(), DB_DRIVER, DatabaseSettings.ORACLE_DATABASE_URL);
    }

    //TODO: Implement the following methods

    /**
     * opens the connection, used by parameterless openConnection()-Method
     *
     * @param username       oracle-username
     * @param password       oracle-password
     * @param dbDriver       oracle-JDBC-driver
     * @param dbServerString connection-String
     *
     * @return the successfully opened connection, or null if failed
     */
    private Connection openConnection(String username, String password, String dbDriver, String dbServerString) {
        try{
           Class.forName("org.postgresql.Driver");
        }catch (Exception e) {
            System.out.println("Driver failure.");
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection(dbServerString, username, password);
        }catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }        
        return con;
    }

    /**
     * Resets the connection (tries to close it and then to re-open the connection)
     *
     * @return the oracle sql.connection
     */
    @Override
    public Connection resetConnection() {
        closeConnection();
        return openConnection();
    }

    /**
     * Closes the connection
     */
    @Override
    public void closeConnection() {
        try{
            con.close();
        }catch(SQLException e){
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
    }
}
