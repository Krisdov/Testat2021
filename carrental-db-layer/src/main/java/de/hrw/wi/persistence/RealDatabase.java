package de.hrw.wi.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hrw.wi.databaseSetup.DatabaseConfiguration;
import de.hrw.wi.persistence.dto.BookingDTO;
import de.hrw.wi.types.Datum;

/**
 * 
 * @author Andriessens
 *
 */
public class RealDatabase implements DatabaseReadInterface, DatabaseWriteInterface {

    private static final String STR_MINUS = "-";
    private static final String STR_CAR_BRAND_COULD_NOT_BE_READ = "Car brand could not be read.";
    private static final String STR_BOOKINGS_COULD_NOT_BE_READ = "Bookings could not be read.";
    private static final String STR_ERR_BOOKING_COULD_NOT_BE_INSERTED =
            "Booking could not be inserted.";
    private static final String STR_LEADING_0 = "0";
    private static final String STR_ERR_CAR_COULD_NOT_BE_ADDED = "Car could not be added.";
    private static final String STR_ERR_BOOKING_COULD_NOT_BE_UPDATED =
            "Booking could not be updated.";

    private static final String STR_CONNECTION_CANNOT_BE_CLOSED =
            "Database connection cannot be closed.";

    private static final String DB_URL = DatabaseConfiguration.getDbUrl();
    private static final String DB_USER = DatabaseConfiguration.getDBUser();
    private static final String DB_PASSWORD = DatabaseConfiguration.getDBPassword();

    /**
     * Initializes database by loading engine driver, connecting and closing again
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public RealDatabase() throws SQLException, ClassNotFoundException {

        // Load the HSQL Database Engine JDBC driver
        // hsqldb.jar should be in the class path or made part of the current
        // jar
        Class.forName("org.hsqldb.jdbcDriver");

        // connect to the database. This will load the db files and start the
        // database if it is not already running.
        Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        safeClose(c);
    }

    private ResultSet executeQuery(String sql) throws SQLException {
        Connection c = null;
        try {
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ResultSet rs = c.createStatement().executeQuery(sql);
            c.commit();
            return rs;
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void safeClose(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
            throw new PersistenceException(STR_CONNECTION_CANNOT_BE_CLOSED);
        }
    }

    /**
     * Führt Schreibzugriffe auf die Datenbank durch
     * 
     * @param statementList
     *            Eine Liste von PreparedStatements, die der Reihe nach ausgeführt werden
     * @param c
     *            Die JDBC-Verbindung zur Datenbank
     * @return Die Anzahl der veränderten Datensätze oder -1, wenn ein Fehler aufgetreten ist
     * @throws SQLException
     */
    private int executePreparedStatementsUpdate(Connection c, List<PreparedStatement> statementList)
            throws SQLException {
        if (c == null) {
            return -1;
        }
        try {
            c.setAutoCommit(false);

            int result = -1;
            int resultSum = 0;
            Iterator<PreparedStatement> it = statementList.iterator();
            if (it.hasNext()) {
                do {
                    PreparedStatement statement = it.next();
                    result = statement.executeUpdate();
                    resultSum += result;
                } while (it.hasNext() && result > 0);
            }

            if (result != 0) {
                c.commit();
            } else {
                c.rollback();
                resultSum = 0;
            }

            return resultSum;
        } catch (SQLException e) {
            c.rollback();
        } finally {
            safeClose(c);
        }
        return 0;
    }

    private List<String> getResultAsStringList(String sql) {
        List<String> list = new ArrayList<String>();
        try {
            ResultSet result = executeQuery(sql);
            while (result.next())
                list.add(result.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<String> getResultAsStringList(ResultSet result) {
        List<String> list = new ArrayList<String>();
        try {
            while (result.next())
                list.add(result.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private String getString(ResultSet result) throws SQLException {
        if (result.next()) {
            return result.getString(1);
        } else {
            return "";
        }
    }

    /**
     * 
     * @param datum
     *            Das Datum als Typ "Datum"
     * @return Umgerechnet in String "JJJJ-MM-TT"
     */
    private String convertDate(Datum datum) {
        return Integer.toString(datum.getJahr()) + STR_MINUS
                + ((datum.getMonat() < 10) ? STR_LEADING_0 : "")
                + Integer.toString(datum.getMonat()) + STR_MINUS
                + ((datum.getTagImMonat() < 10) ? STR_LEADING_0 : "")
                + Integer.toString(datum.getTagImMonat());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCar(String carId, String brand) throws PersistenceException {
        if (!carId.equals("") && !brand.equals("")) {

            Connection c = null;
            try {
                c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String sqlStr = "INSERT INTO CARS VALUES(?, ?, ?);";
                PreparedStatement statement = c.prepareStatement(sqlStr);
                statement.setString(1, carId);
                statement.setString(2, brand);
                statement.setBoolean(3, true);
                int res = executePreparedStatementsUpdate(c, Arrays.asList(statement));

                if (res == 0) {
                    throw new PersistenceException(STR_ERR_CAR_COULD_NOT_BE_ADDED);
                }
            } catch (SQLException e) {
                throw new PersistenceException(STR_ERR_CAR_COULD_NOT_BE_ADDED);
            } finally {
                safeClose(c);
            }
        } else {
            throw new PersistenceException(STR_ERR_CAR_COULD_NOT_BE_ADDED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAllCars() {
        return new HashSet<String>(getResultAsStringList("SELECT id FROM CARS;"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCarBrand(String carId) {
        Connection c = null;

        try {
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sqlStr = "SELECT brand FROM CARS WHERE id=?;";
            PreparedStatement statement = c.prepareStatement(sqlStr);
            statement.setString(1, carId);
            return getString(statement.executeQuery());
        } catch (SQLException e) {
            throw new PersistenceException(STR_CAR_BRAND_COULD_NOT_BE_READ);
        } finally {
            safeClose(c);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<BookingDTO> getBookingsForCarAsDTOs(String carId) {
        Set<BookingDTO> list = new HashSet<BookingDTO>();

        Connection c = null;
        try {
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sqlStr = "SELECT start, end, state, customer_id FROM BOOKINGS WHERE car_id=?;";
            PreparedStatement statement = c.prepareStatement(sqlStr);
            statement.setString(1, carId);

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Datum start = new Datum(result.getDate(1).toLocalDate().getYear(),
                        result.getDate(1).toLocalDate().getMonthValue(),
                        result.getDate(1).toLocalDate().getDayOfMonth());
                Datum end = new Datum(result.getDate(2).toLocalDate().getYear(),
                        result.getDate(2).toLocalDate().getMonthValue(),
                        result.getDate(2).toLocalDate().getDayOfMonth());
                BookingDTO dto =
                        new BookingDTO(result.getInt(3), carId, start, end, result.getString(4));
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new PersistenceException(STR_BOOKINGS_COULD_NOT_BE_READ);
        } finally {
            safeClose(c);
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCarAvailable(String carId, Datum from, Datum to) {
        Connection c = null;

        try {
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // Suche vorhandene Buchungen, deren Start-Datum zwischen from und to
            // liegt
            String sqlStr =
                    "SELECT car_id FROM BOOKINGS where car_id = ? AND start >= ? AND start<= ?;";
            PreparedStatement statement = c.prepareStatement(sqlStr);
            statement.setString(1, carId);
            statement.setString(2, convertDate(from));
            statement.setString(3, convertDate(to));
            List<String> bookings1 = getResultAsStringList(statement.executeQuery());

            // Suche vorhandene Buchungen, deren End-Datum zwischen from und to
            // liegt
            sqlStr = "SELECT car_id FROM BOOKINGS where car_id=? AND end >= ? AND end<= ?;";
            statement = c.prepareStatement(sqlStr);
            statement.setString(1, carId);
            statement.setString(2, convertDate(from));
            statement.setString(3, convertDate(to));
            List<String> bookings2 = getResultAsStringList(statement.executeQuery());

            // Suche vorhandene Buchungen, deren Start-Datum vor from und deren
            // End-Datum nach to liegen
            sqlStr = "SELECT car_id FROM BOOKINGS where car_id=? AND start <= ? AND end >= ?;";
            statement = c.prepareStatement(sqlStr);
            statement.setString(1, carId);
            statement.setString(2, convertDate(from));
            statement.setString(3, convertDate(to));
            List<String> bookings3 = getResultAsStringList(statement.executeQuery());

            return (bookings1.size() == 0) && (bookings2.size() == 0) && (bookings3.size() == 0);

        } catch (SQLException e) {
            throw new PersistenceException(STR_BOOKINGS_COULD_NOT_BE_READ);
        } finally {
            safeClose(c);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> findAvailableCar(Datum from, Datum to) {
        Set<String> cars = getAllCars();
        Set<String> availCars = new HashSet<String>();
        for (String id : cars) {
            if (isCarAvailable(id, from, to)) {
                availCars.add(id);
            }
        }
        return availCars;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAllCustomers() {
        return new HashSet<String>(getResultAsStringList("SELECT id FROM CUSTOMERS;"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFirstName(String customerId) {
        Connection c = null;

        try {
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sqlStr = "SELECT firstname FROM CUSTOMERS WHERE id=?";
            PreparedStatement statement = c.prepareStatement(sqlStr);
            statement.setString(1, customerId);
            return getString(statement.executeQuery());
        } catch (SQLException e) {
            throw new PersistenceException("Customer first name could not be read.");
        } finally {
            safeClose(c);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLastName(String customerId) {
        Connection c = null;

        try {
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sqlStr = "SELECT name FROM CUSTOMERS WHERE id=?;";
            PreparedStatement statement = c.prepareStatement(sqlStr);
            statement.setString(1, customerId);
            return getString(statement.executeQuery());
        } catch (SQLException e) {
            throw new PersistenceException("Customer last name could not be read.");
        } finally {
            safeClose(c);
        }
    }

    /**
     * Fügt eine neue Buchung in die Datenbank ein. Überprüft nicht, ob es die Buchung schon gibt
     * oder ob die Werte korrekt sind. Darf nicht direkt aufgerufen werden, nur über
     * <code>insertBookingForCar()</code>.
     * 
     * @param carId
     *            Kfz-Kennzeichen des Autos
     * @param customerId
     *            Kundennummer des Kunden, für den die Buchung gemacht wird
     * @param from
     *            Startdatum der Buchung
     * @param to
     *            Enddatum der Buchung
     * @param state
     *            Der Status der Buchung (vgl. <code>DatabaseReadInterface</code>)
     */
    private void insertBookingForCar(String carId, String customerId, Datum from, Datum to,
            int state) {
        Connection c = null;
        try {
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sqlStr = "INSERT INTO BOOKINGS VALUES(?, ?, ?, ?, ?);";
            PreparedStatement statement = c.prepareStatement(sqlStr);
            statement.setString(1, carId);
            statement.setString(2, customerId);
            statement.setString(3, convertDate(from));
            statement.setString(4, convertDate(to));
            statement.setInt(5, state);

            int res = executePreparedStatementsUpdate(c, Arrays.asList(statement));

            if (res == 0) {
                throw new PersistenceException(STR_ERR_BOOKING_COULD_NOT_BE_INSERTED);
            }
        } catch (SQLException e) {
            throw new PersistenceException(STR_ERR_BOOKING_COULD_NOT_BE_INSERTED);
        } finally {
            safeClose(c);
        }
    }

    /**
     *
     * Aktualisiert eine vorhandene Buchung in der Datenbank mit neuen Werten. Überprüft nicht, ob
     * es die Buchung schon gibt oder ob die Werte korrekt sind. Darf deshalb nicht direkt
     * aufgerufen werden, nur über <code>insertBookingForCar()</code>.
     * 
     * @param carId
     *            Kfz-Kennzeichen des Autos
     * @param customerId
     *            Kundennummer des Kunden, für den die Buchung gemacht wird
     * @param from
     *            Startdatum der Buchung
     * @param to
     *            Enddatum der Buchung
     * @param state
     *            Der Status der Buchung (vgl. <code>DatabaseReadInterface</code>)
     */
    private void updateBookingForCar(String carId, String customerId, Datum from, Datum to,
            int state) {
        Connection c = null;
        try {
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sqlStr = "UPDATE BOOKINGS SET state=?, customer_id=? "
                    + "WHERE car_id=? AND start=? AND end=?;";
            PreparedStatement statement = c.prepareStatement(sqlStr);
            statement.setInt(1, state);
            statement.setString(2, customerId);
            statement.setString(3, carId);
            statement.setString(4, convertDate(from));
            statement.setString(5, convertDate(to));

            int res = executePreparedStatementsUpdate(c, Arrays.asList(statement));

            if (res == 0) {
                throw new PersistenceException(STR_ERR_BOOKING_COULD_NOT_BE_UPDATED);
            }
        } catch (SQLException e) {
            throw new PersistenceException(STR_ERR_BOOKING_COULD_NOT_BE_UPDATED);
        } finally {
            safeClose(c);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upsertBookingForCar(String carId, String customerId, Datum from, Datum to,
            int state) {

        if (carId.equals("") || customerId.equals("")) {
            throw new PersistenceException("Illegal values, booking could not be updated.");
        }

        // Prüfen, ob Buchung gefunden werden kann
        boolean foundBooking = false;
        Set<BookingDTO> dtos = getBookingsForCarAsDTOs(carId);
        Iterator<BookingDTO> dtoIt = dtos.iterator();
        while (!foundBooking && dtoIt.hasNext()) {
            BookingDTO dto = dtoIt.next();
            if ((dto.getFrom().equals(from)) && (dto.getTo().equals(to))) {
                foundBooking = true;
            }
        }

        if (foundBooking) {
            // Buchung wurde gefunden, UPDATE
            updateBookingForCar(carId, customerId, from, to, state);
        } else {
            // INSERT
            insertBookingForCar(carId, customerId, from, to, state);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<BookingDTO> getAllBookingsAsDTOs() {
        Set<BookingDTO> list = new HashSet<BookingDTO>();

        try {
            ResultSet result =
                    executeQuery("SELECT start, end, state, customer_id, car_id FROM BOOKINGS;");
            while (result.next()) {
                Datum start = new Datum(result.getDate(1).toLocalDate().getYear(),
                        result.getDate(1).toLocalDate().getMonthValue(),
                        result.getDate(1).toLocalDate().getDayOfMonth());
                Datum end = new Datum(result.getDate(2).toLocalDate().getYear(),
                        result.getDate(2).toLocalDate().getMonthValue(),
                        result.getDate(2).toLocalDate().getDayOfMonth());
                BookingDTO dto = new BookingDTO(result.getInt(3), result.getString(5), start, end,
                        result.getString(4));
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new PersistenceException(STR_BOOKINGS_COULD_NOT_BE_READ);
        }
        return list;
    }
}
