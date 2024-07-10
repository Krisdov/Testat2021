/**
 * 
 */
package de.hrw.wi.business.bookings;

/**
 * @author andriesc
 *
 */
public class CarInReturn implements BookingState {
    private Booking booking;

    /**
     * 
     * @param booking
     *            the booking object this state object belongs to
     */
    public CarInReturn(Booking booking) {
        this.booking = booking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void returnCar() {
        throw new IllegalStateException("Car has already been returned.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeBooking() {
        booking.setState(new BookingClosed(booking));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOpen() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCarInReturn() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() {
        return false;
    }

}
