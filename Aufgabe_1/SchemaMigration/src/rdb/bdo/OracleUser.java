/*
 *  Copyright 2014 Rainer Stoermer
 */
package rdb.bdo;

/**
 * POJO-BDO for the login-credentials for the Oracle-DBMS. Realized as a singleton to have only one instance at all
 * time.
 *
 * @author Rainer Stoermer
 */
public class OracleUser {

    private static OracleUser singletonInstance = new OracleUser();
    private String username;
    private String password;

    /**
     * Getter for the singleton-Instance
     *
     * @return Instance of User
     */
    public static OracleUser getInstance() {
        return singletonInstance;
    }

    // private Constructor -> singleton requirement
    private OracleUser() {
        this.username = "";
        this.password = "";
    }

    /**
     * Getter for username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for username
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for password
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for password
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * clears the user-credentials out of the singleton
     */
    public void clearUser() {
        username = "";
        password = "";
    }
}
