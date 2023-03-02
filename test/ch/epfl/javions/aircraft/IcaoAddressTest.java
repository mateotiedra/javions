package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class IcaoAddressTest {
    @Test
    void icaoAddressWorksWithCorrectString() {
        assertDoesNotThrow(() -> {
            new IcaoAddress("4B1814");
        });
    }

    @Test
    void icaoAddressWorksWithIncorrectStringBecauseTooLong() {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B18143"));
    }

    @Test
    void icaoAddressWorksWithIncorrectStringBecauseTooShort() {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B181"));
    }

    @Test
    void icaoAddressWorksWithIncorrectStringBecauseIncorrectCharacter() {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4G181"));
    }

    @Test
    void icaoAddressWorksWithIncorrectStringBecauseEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress(""));
    }
}
