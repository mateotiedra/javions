package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftRegistrationTest {
    @Test
    void aircraftRegistrationTestWorksWithCorrectString() {
        assertDoesNotThrow(() -> {
            new AircraftRegistration("HB-JDC");
        });
    }

    @Test
    void aircraftRegistrationTestWorksWithIncorrectStringBecauseInvalidCharacter() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("HB-JDC]"));
    }

    @Test
    void aircraftRegistrationTestWorksWithIncorrectStringBecauseEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
    }
}
