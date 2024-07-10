/**
 * 
 */
package de.hrw.wi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.hrw.wi.business.Car;
import de.hrw.wi.persistence.DatabaseReadInterface;
import de.hrw.wi.persistence.DatabaseWriteInterface;

/**
 * Teste CarRentalServiceImpl mit Mocking
 * 
 * TODO A9 Diese Testklasse soll Mockito zum Test von CarRentalServiceImpl mit Mock-Objekten ohne
 * Datenbankschicht verwenden. Es ist erst ein Testfall vorhanden, testGetAllCars(), allerdings wird
 * die Testklasse noch nicht fertig für die Durchführung von Tests initialisiert. Im Einzelnen:
 *
 * a) Vervollständigen Sie die Initialisierung so, dass der Testfall testGetAllCars() korrekt
 * durchläuft und dass die Initialisierung für weitere Testfälle in der Klasse nutzbar wäre (wählen
 * Sie also die richtige Stelle für die Initialisierung). Nutzen Sie die when-thenReturn-Technik so,
 * dass der Aufruf von carRentalService. getAllCars() im Test hinreichend durch Mocking mit
 * Testdaten versorgt wird. Vorhanden dafür sind bereits zwei Attribute dbReadMock und dbWriteMock
 * sowie die vollständigen Testdaten.
 * 
 * b) Initialisierung Sie das Attribut carRentalService mit einem Objekt des Typs
 * CarRentalServiceImpl so, dass die Mock-Objekte auch verwendet werden.
 * 
 * @author Andriessens
 *
 */
public class CarRentalServiceImplTest {

    /*******************
     * Testdaten ANFANG
     *******************/
    private static final String LP_UL_M_123 = "UL-M 123";
    private static final String LP_RV_HS_111 = "RV-HS 111";
    /*******************
     * Testdaten ENDE
     *******************/

    private CarRentalServiceInterface carRentalService;
    private DatabaseReadInterface dbReadMock;
    private DatabaseWriteInterface dbWriteMock;

    @Test
    public void testGetAllCars() {
        Set<Car> cars = carRentalService.getAllCars();
        assertEquals(2, cars.size());
        boolean foundRVHS111 = false;
        boolean foundULM123 = false;
        for (Car car : cars) {
            switch (car.getId()) {
                case LP_RV_HS_111:
                    foundRVHS111 = true;
                    break;
                case LP_UL_M_123:
                    foundULM123 = true;
                    break;
                default:
                    break;
            }
        }
        assertTrue(foundRVHS111);
        assertTrue(foundULM123);
    }

}
