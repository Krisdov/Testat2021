/**
 * 
 */
package de.hrw.wi.databaseSetup;

/**
 * Stores database configuration like database connection string, user, password at a central point.
 * Thus, it can be changed centrally while being accessible from all units needing it: We do not
 * need to spread it over the whole system.
 * 
 * @author Andriessens
 *
 */
public final class DatabaseConfiguration {

    private static final String DB_DRIVER = "jdbc:hsqldb:";
    private static final String DB_URL_IN_PROCESS_MODE_NORMAL_PREFIX = "file:";
    private static final String DB_URL_IN_PROCESS_MODE_NORMAL_INITIAL_PATH =
            "../carrental-db-layer/database/carrental_db";
    private static final String DB_URL_SERVER_MODE_NORMAL_PREFIX = "hsql:";
    private static final String DB_URL_SERVER_MODE_NORMAL_INITIAL_PATH = "//localhost/";
    private static final String DB_CONFIG_OPTION_ALWAYS_SHUTDOWN = ";shutdown=";
    private static final String DB_CONFIG_OPTION_WRITE_DELAY = ";hsqldb.write_delay=";
    private static final String DB_TRUE = "true";
    private static final String DB_FALSE = "false";
    private static final String DB_USER_INITIAL = "sa";
    private static final String DB_PASSWORD_INITIAL = "";

    private DatabaseConfiguration() {
        // prevent calling of constructor, therefore visibility private
    }

    /**
     * States the database can be used in.
     * 
     * @author Andriessens
     *
     */
    public enum DBMode {
        IN_PROCESS, SERVER
    };

    private static boolean immediateShutdown = true;
    private static boolean writeDelay = false;
    private static DBMode dbMode = DBMode.IN_PROCESS;
    private static String dbUser = DB_USER_INITIAL;
    private static String dbPassword = DB_PASSWORD_INITIAL;
    private static String dbPath = DB_URL_IN_PROCESS_MODE_NORMAL_INITIAL_PATH;
    private static String dbUrl = DB_DRIVER + DB_URL_IN_PROCESS_MODE_NORMAL_PREFIX + dbPath;

    /**
     * 
     * @return the user name to use in this database configuration
     */
    public static String getDBUser() {
        return dbUser;
    }

    /**
     * 
     * @return the password for the user of this database configuration
     */
    public static String getDBPassword() {
        return dbPassword;
    }

    /**
     * @return <code>true</code> if each connection is shutdown again immediately,
     *         <code>false</code> otherwise
     */
    public static boolean isImmediateShutdownActive() {
        return immediateShutdown;
    }

    /**
     * @param flag
     *            <code>true</code> if each connection should be shutdown again immediately,
     *            <code>false</code> otherwise
     */
    public static void setImmediateShutdown(boolean flag) {
        immediateShutdown = flag;
    }

    /**
     * @return <code>true</code> if delay on write is active, <code>false</code> otherwise
     */
    public static boolean isWriteDelayActive() {
        return writeDelay;
    }

    /**
     * @param flag
     *            <code>true</code> if delay on write should be active, <code>false</code> otherwise
     */
    public static void setWriteDelay(boolean flag) {
        writeDelay = flag;
    }

    /**
     * @return the dbUrl
     */
    public static String getDbUrl() {
        return dbUrl;
    }

    /**
     * @param newDbPath
     *            the new path of the database
     */
    public static void setDbPath(String newDbPath) {
        dbPath = newDbPath;
        switch (dbMode) {
            case IN_PROCESS:
                dbUrl = DB_DRIVER + DB_URL_IN_PROCESS_MODE_NORMAL_PREFIX + dbPath;
                break;
            case SERVER:
                dbUrl = DB_DRIVER + DB_URL_SERVER_MODE_NORMAL_PREFIX + dbPath;
                break;
            default:
                dbUrl = null;
                break;
        }
        if (dbUrl != null) {
            if (isImmediateShutdownActive()) {
                dbUrl += DB_CONFIG_OPTION_ALWAYS_SHUTDOWN + DB_TRUE;
            } else {
                dbUrl += DB_CONFIG_OPTION_ALWAYS_SHUTDOWN + DB_FALSE;
            }
            if (isWriteDelayActive()) {
                dbUrl += DB_CONFIG_OPTION_WRITE_DELAY + DB_TRUE;
            } else {
                dbUrl += DB_CONFIG_OPTION_WRITE_DELAY + DB_FALSE;
            }
        }
    }

    /**
     * Switches the database configuration to another run mode. For each run mode, there might be a
     * different connection string and a different kind of path in his connection string. Therefore,
     * this method resets the database path to the initial value!
     * 
     * @param newDbMode
     *            the new mode of the database
     */
    public static void setDbMode(DBMode newDbMode) {
        dbMode = newDbMode;

        switch (dbMode) {
            case IN_PROCESS:
                setDbPath(DB_URL_IN_PROCESS_MODE_NORMAL_INITIAL_PATH);
                break;
            case SERVER:
                setDbPath(DB_URL_SERVER_MODE_NORMAL_INITIAL_PATH);
                break;
            default:
                dbUrl = null;
                break;
        }
    }

    /**
     * @param newDbPassword
     *            the new password for the database user of this configuration
     */
    public static void setDbPassword(String newDbPassword) {
        dbPassword = newDbPassword;
    }

}
