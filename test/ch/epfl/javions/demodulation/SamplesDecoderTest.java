package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamplesDecoderTest {

    @Test
    void constructorWorksWithNullStream() {
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 1));
    }

    @Test
    void constructorWorksWithBatchSizeEqualsZero() {
        String url = getClass().getResource("/samples.bin").getFile();
        try (InputStream stream = new FileInputStream(url)) {
            assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(stream, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
