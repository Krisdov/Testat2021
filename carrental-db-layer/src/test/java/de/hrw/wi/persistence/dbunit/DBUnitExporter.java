package de.hrw.wi.persistence.dbunit;

/*
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import de.hrw.wi.databaseSetup.DatabaseConfiguration;
*/

/**
 * @author andriesc
 *
 */
public final class DBUnitExporter {

    /*
    // JDBC-URL für die Datenbank, kann direkt so verwendet werden
    private static final String DB_URL = DatabaseConfiguration.getDbUrl();
    // Benutzernamen für die Datenbank, kann direkt so verwendet werden
    private static final String USER = DatabaseConfiguration.getDBUser();
    // Passwort für die Datenbank, kann direkt so verwendet werden
    private static final String PASSWORD = DatabaseConfiguration.getDBPassword();
    */
    
    /**
     * Preventing call of a constructor of this helper class: Therefore, set visibility to private.
     */
    private DBUnitExporter() {

    }

    /**
     * @param args
     *                 will not be evaluated
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        /*
        // database connection
        Connection jdbcConnection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        // DBUnit's DatabaseSequenceFilter automatically determines table order
        // using foreign keys information. Without, IDatabaseTester.onSetup()
        // would produce constraint violation exceptions on foreign keys.
        ITableFilter filter = new DatabaseSequenceFilter(connection);

        // full database export
        IDataSet fullDataSet = new FilteredDataSet(filter, connection.createDataSet());

        FlatXmlDataSet.write(fullDataSet, new FileOutputStream("db_full_export.xml"));
        connection.close();
        jdbcConnection.close();
        */
    }
}
