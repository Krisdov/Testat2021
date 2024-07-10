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
    	//TODO Auto-generated method stub

    }

    @Override
    public void returnCar() {
    	//TODO Auto-generated method stub

    }

    @Override
    public void closeBooking() {
    	//TODO Auto-generated method stub

    }

    @Override
    public boolean isOpen() {
    	//TODO Auto-generated method stub
    	return false; 

    }

    @Override
    public boolean isCarInReturn() {
    	//TODO Auto-generated method stub
    	return false;
    }

    @Override
    public boolean isClosed() {
    	//TODO Auto-generated method stub
    	return false; 
    }

}
