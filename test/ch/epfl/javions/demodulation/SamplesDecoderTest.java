package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamplesDecoderTest {

    @Test
    void constructorWorksWithNullStream() {
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 1));
    }

    /*@Test
    void constructorWorksWithBatchSizeEqualsZero() {
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(null, 0));
    }*/

}
