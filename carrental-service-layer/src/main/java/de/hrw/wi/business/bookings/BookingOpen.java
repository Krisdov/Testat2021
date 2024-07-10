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


    public BookingOpen(Booking booking) {
    	//implementiert in anderem Zweig

    }

    @Override
    public void returnCar() {
        //implementiert in anderem Zweig

    }

    @Override
    public void closeBooking() {
        //implementiert in anderem Zweig

    }

    @Override
    public boolean isOpen() {
        //implementiert in anderem Zweig
    	return false; 

    }

    @Override
    public boolean isCarInReturn() {
        //implementiert in anderem Zweig
    	return false;
    }

    @Override
    public boolean isClosed() {
        //implementiert in anderem Zweig
    	return false; 
    }

}
