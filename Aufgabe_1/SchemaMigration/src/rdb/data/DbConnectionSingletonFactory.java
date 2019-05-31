/*
 *  Copyright 2014 Rainer Stoermer
 */
package rdb.data;

/**
 * SingletonFactory to use the DbConnection-Classes as a singleton.
 *
 * @author Rainer Stoermer
 */
public class DbConnectionSingletonFactory {

    static private DbConnectionSingletonFactory dbFactory;

    private DbConnectionPostgres dbConPostgres;
    private DbConnectionOracle dbConOracle;

    // private constructor, we don't want the class to be instantiated from others.
    private DbConnectionSingletonFactory() {

    }

    /**
     * Singleton Getter
     *
     * @return the singletonFactory-Instance
     */
    public synchronized static DbConnectionSingletonFactory getDbConnectionSingletonFactory() {
        if (dbFactory == null) {
            dbFactory = new DbConnectionSingletonFactory();
        }
        return dbFactory;
    }

    /**
     * Getter for the DbConnection, use parameter to choose Oracle-Instance or PostgreSQL-Instance
     *
     * @param dbms postgres or oracle
     *
     * @return DbConnection for given dbms-parameter as singleton
     */
    public synchronized DbConnection getDbConnection(String dbms) {

        switch (dbms) {
            case "postgres":
                if (this.dbConPostgres == null) {
                    this.dbConPostgres = new DbConnectionPostgres();
                }
                return this.dbConPostgres;
            case "oracle":
                if (this.dbConOracle == null) {
                    this.dbConOracle = new DbConnectionOracle();
                }
                return this.dbConOracle;
        }
        return null;
    }
}
