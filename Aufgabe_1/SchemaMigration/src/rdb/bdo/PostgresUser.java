/*
 *  Copyright 2014 Rainer Stoermer
 */
package rdb.bdo;

/**
 * POJO-BDO for the login-credentials for the PostgreSQL-DBMS. Realized as a singleton to have only one instance at all
 * time.
 *
 * @author Rainer Stoermer
 */
public class PostgresUser {

    private static PostgresUser singletonInstance = new PostgresUser();
    private String username;
    private String password;

    /**
     * Getter for the singleton-Instance
     *
     * @return
     */
    public static PostgresUser getInstance() {
        return singletonInstance;
    }

    // private Constructor -> singleton requirement
    private PostgresUser() {
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
     * @return password
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
