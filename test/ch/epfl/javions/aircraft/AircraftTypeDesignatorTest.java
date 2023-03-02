package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftTypeDesignatorTest {
    @Test
    void aircraftTypeDesignatorWorksWithCorrectString() {
        assertDoesNotThrow(() -> {
            new AircraftTypeDesignator("A20N");
        });
    }

    @Test
    void aircraftTypeDesignatorWorksWithEmptyString() {
        assertDoesNotThrow(() -> {
            new AircraftTypeDesignator("");
        });
    }

    @Test
    void aircraftTypeDesignatorWorksWithIncorrectStringBecauseInvalidCharacter() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("A20N-"));
    }

    @Test
    void aircraftTypeDesignatorWorksWithIncorrectStringBecauseTooShort() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("A"));
    }

    @Test
    void aircraftTypeDesignatorWorksWithIncorrectStringBecauseTooLong() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("A20NLO"));
    }
}
