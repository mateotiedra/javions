package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AircraftDataBaseTest {
    // Test case 1: IcaoAddress exists in the zip file
    @Test
    void aircraftDataBaseWorksWithCorrectString() throws IOException {
        AircraftDataBase database = new AircraftDataBase("/aircraft.zip");
        IcaoAddress address1 = new IcaoAddress("CP-3080");
        AircraftData expected1 = new AircraftData(new AircraftRegistration("CP-3080"), new AircraftTypeDesignator("C208"), "CESSNA 208 Caravan", new AircraftDescription("L1T"), WakeTurbulenceCategory.LIGHT);
        AircraftData actual1 = database.get(address1);
        assertEquals(expected1, actual1);
    }/*
    @Test
    void aircraftDataBaseDoesNotWorksWithInexistantIcao() throws IOException{
        // Test case 2: IcaoAddress does not exist in the zip file
        IcaoAddress address2 = new IcaoAddress("B00001");
        AircraftData expected2 = null;
        AircraftData actual2 = database.get(address2);
        assertEquals(expected2, actual2);
    }*/
}
