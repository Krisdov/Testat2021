/**
 * 
 */
package de.hrw.wi.business.bookings;

/**
 * TODO A7a) Im Branch feature: Vervollständigen Sie die Implementierung von BookingOpen und
 * entfernen Sie dabei die Kommentare „// TO DO Auto-generated method stub“. Beachten Sie: Die
 * Methode closeBooking() der Klasse wirft bei jedem Aufruf eine IllegalStateException.
 * 
 * TODO A11a) Im Branch newFeature: Entfernen Sie alle Kommentare „// TO DO Auto-generated method
 * stub“ und ersetzen Sie diese jeweils durch den Kommentar „// implementiert in anderem Zweig“.
 * 
 * @author andriesc
 *
 */
public class BookingOpen implements BookingState {

    private Booking booking;

    public BookingOpen(Booking booking) {
        booking = this.booking;
    }
    private String bookingNotEvenInReturn = "Car is not even in Return.";

    @Override
    public void returnCar() {
    	booking.setState(new CarInReturn(booking));

    }

    @Override
    public void closeBooking() {
    	throw new IllegalStateException(bookingNotEvenInReturn);

    }

    @Override
    public boolean isOpen() {
    	return true;

    }

    @Override
    public boolean isCarInReturn() {
    	return false;
    }

    @Override
    public boolean isClosed() {
    	return false; 
    }

}
