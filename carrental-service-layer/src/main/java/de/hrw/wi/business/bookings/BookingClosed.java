package de.hrw.wi.business.bookings;

/**
 * @author andriesc
 *
 */
public class BookingClosed implements BookingState {
    @SuppressWarnings("unused")
    private Booking booking;
    private final String bookingAlreadyClosed = "Booking has already been closed.";

    /**
     * 
     * @param booking
     *            the booking this object is a state for
     */
    public BookingClosed(Booking booking) {
        this.booking = booking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void returnCar() {
        throw new IllegalStateException(bookingAlreadyClosed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeBooking() {
        throw new IllegalStateException(bookingAlreadyClosed);
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
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() {
        return true;
    }

}
