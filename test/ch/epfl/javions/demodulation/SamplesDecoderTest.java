package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void readBatchWorksNotCorrespondingBatchSize() {
        String url = getClass().getResource("/samples.bin").getFile();
        try (InputStream stream = new FileInputStream(url)) {
            SamplesDecoder samplesDecoder = new SamplesDecoder(stream, 1200);
            assertThrows(IllegalArgumentException.class, () -> samplesDecoder.readBatch(new short[1201]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void readBatchReturnsCorrectNumberOfSampleRead() {
        String url = getClass().getResource("/samples.bin").getFile();
        for (int batchSize : new int[]{3000, 2402, 1200}) {
            try (InputStream stream = new FileInputStream(url)) {
                SamplesDecoder samplesDecoderWithBigArray = new SamplesDecoder(stream, batchSize);
                short[] batch = new short[batchSize];
                assertEquals(Math.min(2402, batchSize), samplesDecoderWithBigArray.readBatch(batch));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void readBatchReadsCorrectlyTheFirst10Sample() {
        String url = getClass().getResource("/samples.bin").getFile();
        try (InputStream stream = new FileInputStream(url)) {
            SamplesDecoder samplesDecoder = new SamplesDecoder(stream, 1200);
            short[] batch = new short[1200];
            samplesDecoder.readBatch(batch);
            short[] expectedValues = {-3, 8, -9, -8, -5, -8, -12, -16, -23, -9};

            for (int i = 0; i < expectedValues.length; i++) {
                assertEquals(expectedValues[i], batch[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReadBatch() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[]{0x00, 0x00, (byte) 0xff, (byte) 0xff});
        SamplesDecoder decoder = new SamplesDecoder(stream, 2);
        short[] batch = new short[2];
        int bytesRead = decoder.readBatch(batch);
        assertEquals(2, bytesRead);
        assertEquals(-2048, batch[0]);
        assertEquals(2047, batch[1]);
    }

    @Test
    void testConstructorThrowsExceptionForNegativeBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            InputStream stream = new ByteArrayInputStream(new byte[]{});
            new SamplesDecoder(stream, -1);
        });
    }

    @Test
    void testConstructorThrowsExceptionForNullInputStream() {
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 2));
    }

    @Test
    void testReadBatchThrowsExceptionForMismatchedBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            InputStream stream = new ByteArrayInputStream(new byte[]{});
            SamplesDecoder decoder = new SamplesDecoder(stream, 2);
            short[] batch = new short[3];
            decoder.readBatch(batch);
        });
    }

}
