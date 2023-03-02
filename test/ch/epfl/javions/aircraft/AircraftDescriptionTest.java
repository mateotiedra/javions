package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDescriptionTest {
    @Test
    void aircraftDescriptionWorksWithCorrectString() {
        assertDoesNotThrow(() -> {
            new AircraftDescription("L2J");
        });
    }

    @Test
    void aircraftDescriptionWorksWithEmptyString() {
        assertDoesNotThrow(() -> {
            new AircraftDescription("");
        });
    }

    @Test
    void aircraftDescriptionWorksWithIncorrectStringBecauseInvalidCharacter() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("22J"));
    }

    @Test
    void aircraftDescriptionWorksWithIncorrectStringBecauseTooShort() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("L2"));
    }

    @Test
    void aircraftDescriptionWorksWithIncorrectStringBecauseTooLong() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("L2JJ"));
    }
}
