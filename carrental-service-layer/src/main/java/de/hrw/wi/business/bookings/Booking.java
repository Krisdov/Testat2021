/**
 * 
 */
package de.hrw.wi.business.bookings;

import de.hrw.wi.business.Car;
import de.hrw.wi.business.Customer;
import de.hrw.wi.types.Datum;

/**
 * Buchung eines Autos als Ausleihe. Das Auto zu einer Buchung kann nicht geändert werden. Eine
 * Buchung bleibt nach Anlage offen, bis das Auto zurückgegeben wird. Eine geschlossene Buchung kann
 * nicht neu geöffnet werden.
 * 
 * @author andriesc
 *
 */
public class Booking {

    private BookingState state;
    private Car car;
    private Customer customer;
    private Datum from;
    private Datum to;

    /**
     * For creating a new booking object: These are states to provide to the constructor.
     * 
     * @author Andriessens
     *
     */
    public enum InitialBookingState {
        OPEN, IN_RETURN, CLOSED;
    }

    /**
     * 
     * @param car
     *            the car the booking is for
     * @param customer
     *            the customer the booking is for
     * @param from
     *            the date the booking starts at
     * @param to
     *            the date the booking ends at
     */
    public Booking(Car car, Customer customer, Datum from, Datum to) {
        this.car = car;
        this.customer = customer;
        this.from = from;
        this.to = to;
        state = new BookingOpen(this);
    }

    /**
     * 
     * @param car
     *            the car the booking is for
     * @param customer
     *            the customer the booking is for
     * @param from
     *            the date the booking starts at
     * @param to
     *            the date the booking ends at
     * @param initialState
     *            a <code>DatabaseReadInterface</code> state for the booking
     */
    public Booking(Car car, Customer customer, Datum from, Datum to,
            InitialBookingState initialState) {
        this.car = car;
        this.customer = customer;
        this.from = from;
        this.to = to;
        switch (initialState) {
            case OPEN:
                this.state = new BookingOpen(this);
                break;
            case IN_RETURN:
                this.state = new CarInReturn(this);
                break;
            case CLOSED:
                this.state = new BookingClosed(this);
                break;
            default:
                throw new IllegalStateException("Illegal state for booking.");
        }
    }

    /**
     * changes state: car has been returned
     */
    public void returnCar() {
        state.returnCar();
    }

    /**
     * changes state: booking is closed
     */
    public void closeBooking() {
        state.closeBooking();
    }

    /**
     * 
     * @return <code>true</code> if booking is open
     */
    public boolean isOpen() {
        return state.isOpen();
    }

    /**
     * 
     * @return <code>true</code> if car is currently returning but returning has not yet finished
     */
    public boolean isCarInReturn() {
        return state.isCarInReturn();
    }

    /**
     * 
     * @return <code>true</code> if booking is closed
     */
    public boolean isClosed() {
        return state.isClosed();
    }

    /**
     * 
     * @param state
     */
    protected void setState(BookingState state) {
        this.state = state;
    }

    /**
     * 
     * @return car this booking belongs to
     */
    public Car getCar() {
        return car;
    }

    /**
     * 
     * @return date the booking starts at
     */
    public Datum getFrom() {
        return from;
    }

    /**
     * 
     * @return date the booking ends at
     */
    public Datum getTo() {
        return to;
    }

    /**
     * 
     * @param from
     *            new date the booking should now start at
     */
    public void setFrom(Datum from) {
        this.from = from;
    }

    /**
     * 
     * @param to
     *            new date the booking should now end at
     */
    public void setTo(Datum to) {
        this.to = to;
    }

    /**
     * 
     * @return the customer this booking belongs to
     */
    public Customer getCustomer() {
        return customer;
    }

    
    /***************************************************************************
     ***************************************************************************
     * 
     * Die Methoden unterhalb dieses Kommentars werden im Testat nicht benötigt
     * 
     ***************************************************************************
     ***************************************************************************/

    
    /**
     * Returns a hash code value for the object calculated from the car's hashCode and the from / to
     * date.
     * 
     * @return a hash code value for the object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((car == null) ? 0 : car.hashCode());
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }

    /**
     * Checks if two dates are both equal or null, needed by equals() method
     * 
     * @param firstDate
     *            the first date for checking
     * @param secondDate
     *            the second date for checking
     * @return <code>true</code>, if the two dates are both equal or null, <code>false</code>
     *         otherwise
     */
    private boolean isBothNullOrEquals(Datum firstDate, Datum secondDate) {
        if (firstDate == null) {
            return (secondDate == null);
        } else {
            return firstDate.equals(secondDate);
        }
    }

    /**
     * Two bookings are equal if and only if the car's ID and from / to date are the same.
     * 
     * @param obj
     *            the other booking to compare with
     * @return <code>true</code>, if the car's ID and from / to date are the same,
     *         <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Booking other = (Booking) obj;

        return isBothNullOrEquals(from, other.from) && isBothNullOrEquals(to, other.to);
    }
}
