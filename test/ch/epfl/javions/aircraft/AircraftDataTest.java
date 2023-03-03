package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDataTest {
    @Test
    void aircraftDataTestSuccess(){
        AircraftRegistration registration = new AircraftRegistration("CP-3080");
        AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator("C208");
        String model = "CESSNA 208 Caravan";
        AircraftDescription description = new AircraftDescription("L1T");
        WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.LIGHT;
        AircraftData aircraftData = new AircraftData(registration, typeDesignator, model, description, wakeTurbulenceCategory);

        assertEquals(registration, aircraftData.registration());
        assertEquals(typeDesignator, aircraftData.typeDesignator());
        assertEquals(model, aircraftData.model());
        assertEquals(description, aircraftData.description());
        assertEquals(wakeTurbulenceCategory, aircraftData.wakeTurbulenceCategory());
    }
    @Test
    public void testAircraftDataWithNull() {
        assertThrows(NullPointerException.class, () -> new AircraftData(null, null, null, null, null));
    }
    @Test
    public void testAircraftDataWithSomeNull() {
        AircraftRegistration registration = new AircraftRegistration("CP-3080");
        AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator("C208");
        String model = "CESSNA 208 Caravan";
        AircraftDescription description = new AircraftDescription("L1T");
        WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.LIGHT;

        assertThrows(NullPointerException.class, () -> new AircraftData(null, typeDesignator, model, description, wakeTurbulenceCategory));
        assertThrows(NullPointerException.class, () -> new AircraftData(registration, null, model, description, wakeTurbulenceCategory));
        assertThrows(NullPointerException.class, () -> new AircraftData(registration, typeDesignator, null, description, wakeTurbulenceCategory));
        assertThrows(NullPointerException.class, () -> new AircraftData(registration, typeDesignator, model, null, wakeTurbulenceCategory));
        assertThrows(NullPointerException.class, () -> new AircraftData(registration, typeDesignator, model, description, null));
    }
}
