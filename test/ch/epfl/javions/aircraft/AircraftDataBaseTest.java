package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
/**
 * This class represents the AircraftDataBaseTest
 * @author Kevan Lam (356395)
 */
public class AircraftDataBaseTest {
    @Test
    void aircraftConstructorSuccess(){
        assertDoesNotThrow(() -> new AircraftDatabase("/aircraft.zip"));
    }
    @Test
    void aircraftConstructorFails(){
        assertThrows(NullPointerException.class, () -> new AircraftDatabase(null));
    }
    //Test with data that exist
    @Test
    void aircraftDataBaseWorksWithCorrectString() throws IOException {
        AircraftDatabase database = new AircraftDatabase("/aircraft.zip");
        IcaoAddress address1 = new IcaoAddress("E941FF");
        AircraftData expected1 = new AircraftData(new AircraftRegistration("CP-3080"), new AircraftTypeDesignator("C208"), "CESSNA 208 Caravan", new AircraftDescription("L1T"), WakeTurbulenceCategory.LIGHT);
        AircraftData actual1 = database.get(address1);
        IcaoAddress address3 = new IcaoAddress("440A1E");
        AircraftData expected3 = new AircraftData(new AircraftRegistration("OE-IDJ"), new AircraftTypeDesignator("A320"), "AIRBUS A-320", new AircraftDescription("L2J"), WakeTurbulenceCategory.MEDIUM);
        AircraftData actual3 = database.get(address3);
        assertEquals(expected3, actual3);
        assertEquals(expected1, actual1);
    }
    // Test that does not work cause the data does not exist
    @Test
    void aircraftDataBaseDoesNotWorksWithInexistantIcao() throws IOException{
        AircraftDatabase database = new AircraftDatabase("/aircraft.zip");
        IcaoAddress address2 = new IcaoAddress("B00001");
        AircraftData expected2 = null;
        AircraftData actual2 = database.get(address2);
        assertEquals(expected2, actual2);
    }
}
