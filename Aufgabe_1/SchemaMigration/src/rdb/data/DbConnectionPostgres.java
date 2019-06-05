package rdb.data;

import rdb.bdo.PostgresUser;
import rdb.RDBSchemaMigration;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnectionPostgres implements DbConnection {

    private final String DB_DRIVER = "org.postgresql.Driver";
    private Connection con;

    /**
     * constructor
     */
    protected DbConnectionPostgres() {
    }

    /**
     *
     * @return the postgres sql.connection
     */
    @Override
    public Connection getConnection() {
        return con;
    }

    /**
     * Opens the postgres-sql.connection, using postgresUser-Data and DatabaseSettings for connecting.
     *
     * @return the open sql.connection for the postgres-DBMS
     */
    @Override
    public Connection openConnection() {
        PostgresUser user = PostgresUser.getInstance();
        return openConnection(user.getUsername(),user.getPassword(), DB_DRIVER, DatabaseSettings.POSTGRES_DATABASE_URL);
    }

    //TODO: Implement the following methods

    /**
     * opens the connection, used by parameterless openConnection()-Method
     *
     * @param username       postgres-username
     * @param password       postgres-password
     * @param dbDriver       postgres-JDBC-driver
     * @param dbServerString connection-String
     *
     * @return the successfully opened connection, or null if failed
     */
    private Connection openConnection(String username, String password, String dbDriver, String dbServerString){
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
     * @return the postgres sql.connection
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
