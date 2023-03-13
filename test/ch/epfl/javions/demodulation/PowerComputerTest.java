package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class PowerComputerTest {

    @Test
    public void generalTest() throws Exception {
        // Prepare input stream
        byte[] inputBytes = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);

        // Create PowerComputer object and read batch
        int batchSize = 8;
        PowerComputer powerComputer = new PowerComputer(inputStream, batchSize);
        int[] batch = new int[batchSize];
        int numBatches = powerComputer.readBatch(batch);

        // Verify results
        assertEquals(2, numBatches);
        assertEquals(4844548, batch[0]);
    }

    @Test
    void constructorWorksWithNullStream() {
        assertThrows(NullPointerException.class, () -> new PowerComputer(null, 8));
    }

    @Test
    void constructorWorksWithBatchSizeEqualsZero() {
        String url = getClass().getResource("/samples.bin").getFile();
        try (InputStream stream = new FileInputStream(url)) {
            assertThrows(IllegalArgumentException.class, () -> new PowerComputer(stream, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void readBatchWorksNotCorrespondingBatchSize() {
        String url = getClass().getResource("/samples.bin").getFile();
        try (InputStream stream = new FileInputStream(url)) {
            PowerComputer powerComputer = new PowerComputer(stream, 1200);
            assertThrows(IllegalArgumentException.class, () -> powerComputer.readBatch(new int[1201]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void readBatchReturnsCorrectNumberOfSampleRead() {
        String url = getClass().getResource("/samples.bin").getFile();
        for (int batchSize : new int[]{2416, 1208, 400}) {
            try (InputStream stream = new FileInputStream(url)) {
                PowerComputer powerComputerWithBigArray = new PowerComputer(stream, batchSize);
                int[] batch = new int[batchSize];
                assertEquals(Math.min(1201, batchSize), powerComputerWithBigArray.readBatch(batch));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void readBatchReadsCorrectlyTheFirst10Sample() {
        String url = getClass().getResource("/samples.bin").getFile();
        try (InputStream stream = new FileInputStream(url)) {
            PowerComputer powerComputer = new PowerComputer(stream, 24);
            int[] batch = new int[24];
            powerComputer.readBatch(batch);
            int[] expectedValues = {73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};

            for (int i = 0; i < expectedValues.length; i++) {
                assertEquals(expectedValues[i], batch[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void readBatchReadsCorrectlyTheFirst10SampleButSeparately() {
        String url = getClass().getResource("/samples.bin").getFile();
        try (InputStream stream = new FileInputStream(url)) {
            PowerComputer powerComputer = new PowerComputer(stream, 8);
            int[] batch = new int[8];

            int[] firstHeightExpectedValues = {73, 292, 65, 745, 98, 4226, 12244, 25722};
            powerComputer.readBatch(batch);
            for (int i = 0; i < firstHeightExpectedValues.length; i++) {
                assertEquals(firstHeightExpectedValues[i], batch[i]);
            }

            int[] batchWithLastTwoValues = new int[]{36818, 23825};
            powerComputer.readBatch(batch);
            for (int i = 0; i < batchWithLastTwoValues.length; i++) {
                assertEquals(batchWithLastTwoValues[i], batch[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
