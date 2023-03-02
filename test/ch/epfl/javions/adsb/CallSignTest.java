package ch.epfl.javions.adsb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CallSignTest {
    @Test
    void callSignWorksWithCorrectString() {
        assertDoesNotThrow(() -> {
            new CallSign("R534GS");
        });
    }

    @Test
    void callSignWorksWithEmptyString() {
        assertDoesNotThrow(() -> {
            new CallSign("");
        });
    }

    @Test
    void callSignWorksWithIncorrectStringBecauseInvalidCharacter() {
        assertThrows(IllegalArgumentException.class, () -> new CallSign("R534GH!"));
    }

    @Test
    void callSignWorksWithIncorrectStringBecauseTooLong() {
        assertThrows(IllegalArgumentException.class, () -> new CallSign("R534GSQQ3Q"));
    }
}
