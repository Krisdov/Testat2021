package de.hrw.wi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.dbunit.Assertion;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.hrw.wi.databaseSetup.DatabaseConfiguration;
import de.hrw.wi.persistence.dto.BookingDTO;
import de.hrw.wi.types.Datum;

/**
 * TODO A4 Diese Testklasse soll mit DBUnit arbeiten. Setzen Sie nun folgendes um:
 * 
 * a) DBUnit einbinden. DBUnit ist im db-layer noch nicht als Bibliothek in das Projekt eingebunden.
 * Setzen Sie Maven dafür ein. Merkmale: Organisation org.dbunit, Kennzeichnung des Artefakts ist
 * DBUnit, benötigte Version ist 2.7.2. Definieren Sie außerdem den passenden Scope für DBUnit.
 * 
 * b) DBUnit zur Initialisierung verwenden: Es steht bereits ein XML-Schnappschuss für die Datenbank
 * unter dem Namen db_full_export.xml im Projekt zur Verfügung. Identifizieren Sie die dafür
 * passende Methode in der Klasse DatabaseWriteInterfaceTest und initialisieren Sie dort DBUnit so,
 * dass vor der Erzeugung des Datenbankobjekts und der Ausführung jedes einzelnen Tests die
 * Datenbank mit dem Inhalt von db_full_export.xml überschrieben wird. Verwenden Sie für die
 * Initialisierung die bereits vorbereiteten Attribute DB_URL, USER und PASSWORD.
 * 
 * 
 * @author Andriessens
 *
 */
public class DatabaseWriteInterfaceTest {
    /*******************
     * Testdaten ANFANG
     *******************/
    private static final int TEST_YEAR = 2020;
    private static final String CUSTOMER_ID_00001002 = "00001002";
    private static final String CUSTOMER_ID_00001001 = "00001001";
    private static final String M_LI_200 = "M-LI 200";
    private static final String AUDI = "Audi";
    private static final String RV_TT_777 = "RV-TT 777";
    private static final String DB_FULL_EXPORT_XML = "../carrental-db-layer/db_full_export.xml";
    private static final String DB_FULL_EXPORT_SHORT = "db_full_export.xml";
    /*******************
     * Testdaten ENDE
     *******************/

    // JDBC-URL für die Datenbank, kann direkt so verwendet werden
    private static final String DB_URL = DatabaseConfiguration.getDbUrl();
    // Benutzernamen für die Datenbank, kann direkt so verwendet werden
    private static final String USER = DatabaseConfiguration.getDBUser();
    // Passwort für die Datenbank, kann direkt so verwendet werden
    private static final String PASSWORD = DatabaseConfiguration.getDBPassword();

    DatabaseWriteInterface dbWrite;
    DatabaseReadInterface dbRead;
    IDatabaseTester databaseTester;


    @BeforeEach
    public void setUp() throws Exception {
        databaseTester = new JdbcDatabaseTester("org.hsqldb.jdbcDiver", DB_URL, USER, PASSWORD);
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(DB_FULL_EXPORT_SHORT));
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
        RealDatabase db = new RealDatabase();
        dbWrite = db;
        dbRead = db;
    }

    @Test
    public void testAddCar() {
        assertEquals(5, dbRead.getAllCars().size());
        dbWrite.addCar(RV_TT_777, AUDI);
        assertEquals(6, dbRead.getAllCars().size());
        assertEquals(AUDI, dbRead.getCarBrand(RV_TT_777));
    }

    /**
     * TODO A4c) DBUnit im Test verwenden: Im Test testAddIllegalCar() wird versucht, fälschlich ein
     * Auto ohne Kennzeichen abzuspeichern. Die Datenbankschicht soll dies durch eine Exception vom
     * Typ PersistenceException abwehren. Der Test ist noch nicht ganz vollständig: Vervollständigen
     * Sie ihn so, dass er durchläuft, wenn die Datenbank wie vorgesehen den richtigen Exception-Typ
     * tatsächlich wirft und ansonsten fehlschlägt.
     * 
     * @throws Exception
     */

    @Test
    public void testAddIllegalCar() throws Exception {
    	try {
            dbWrite.addCar("", AUDI);
        } catch (PersistenceException e) {
            IDataSet actualDataSet = databaseTester.getConnection().createDataSet();
            Assertion.assertEquals(new FlatXmlDataSetBuilder().build(new File(DB_FULL_EXPORT_SHORT)), actualDataSet);
        }
    }

    @Test
    public void testBookCar() {
        Datum from = new Datum(TEST_YEAR, 04, 16);
        Datum to = new Datum(TEST_YEAR, 04, 17);
        Set<String> carIds = dbRead.findAvailableCar(from, to);
        assertTrue(carIds.size() > 0);
        String carId = carIds.iterator().next();
        assertTrue(dbRead.findAvailableCar(from, to).contains(carId));
        dbWrite.upsertBookingForCar(carId, CUSTOMER_ID_00001002, from, to,
                DatabaseReadInterface.STATE_OPEN);
        assertFalse(dbRead.findAvailableCar(from, to).contains(carId));
    }

    @Test
    public void testReturnCar() {
        Datum from = new Datum(TEST_YEAR, 04, 16);
        Datum to = new Datum(TEST_YEAR, 04, 26);
        assertFalse(dbRead.findAvailableCar(from, to).contains(M_LI_200));
        dbWrite.upsertBookingForCar(M_LI_200, CUSTOMER_ID_00001001, from, to,
                DatabaseReadInterface.STATE_IN_RETURN);

        // Prüfen ob die Rückgabe erfolgreich war
        Set<BookingDTO> bookings = dbRead.getBookingsForCarAsDTOs(M_LI_200);
        assertEquals(1, bookings.size());
        BookingDTO booking = bookings.iterator().next();
        assertEquals(from, booking.getFrom());
        assertEquals(to, booking.getTo());
        // Hier muss jetzt das Ergebnis true sein
        assertTrue(booking.getState() == DatabaseReadInterface.STATE_IN_RETURN);
    }

    @Test
    public void testCloseBooking() {
        Datum from = new Datum(TEST_YEAR, 04, 16);
        Datum to = new Datum(TEST_YEAR, 04, 26);
        assertFalse(dbRead.findAvailableCar(from, to).contains(M_LI_200));
        dbWrite.upsertBookingForCar(M_LI_200, CUSTOMER_ID_00001001, from, to,
                DatabaseReadInterface.STATE_CLOSED);

        // Prüfen ob die Rückgabe erfolgreich war
        Set<BookingDTO> bookings = dbRead.getBookingsForCarAsDTOs(M_LI_200);
        assertEquals(1, bookings.size());
        BookingDTO booking = bookings.iterator().next();
        assertEquals(from, booking.getFrom());
        assertEquals(to, booking.getTo());
        // Hier muss jetzt das Ergebnis false sein
        assertTrue(booking.getState() == DatabaseReadInterface.STATE_CLOSED);
    }
}
