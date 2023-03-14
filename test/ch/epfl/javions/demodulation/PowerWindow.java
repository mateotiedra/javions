package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;


class PowerWindowTest {
    private static final int BATCH_SIZE = 1 << 16;
    private static final int BATCH_SIZE_BYTES = bytesForPowerSamples(BATCH_SIZE);
    private static final int STANDARD_WINDOW_SIZE = 1200;
    private static final int BIAS = 1 << 11;

    private static int bytesForPowerSamples(int powerSamplesCount) {
        return powerSamplesCount * 2 * Short.BYTES;
    }
    @Test
    public void windowOK() throws IOException {
        String fileName = (getClass().getResource("/samples.bin")).getFile();
        InputStream sampleStream = new FileInputStream(fileName);
        PowerWindow window = new PowerWindow(sampleStream, 6);
        assertEquals(6, window.size());
        assertEquals(73, window.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> window.get(7));
        window.advance();
        assertEquals(292, window.get(0));
        window.advanceBy(3);
        assertEquals(98, window.get(0));
        assertEquals(25722, window.get(3));
        assertEquals(36818, window.get(4));
        assertEquals(23825, window.get(5));
        window.advanceBy(5);
        assertEquals(23825, window.get(0));
        window.advance();
        assertEquals(10730, window.get(0));
    }

    @Test
    public void isFull() throws IOException {
        String fileName = (getClass().getResource("/samples.bin")).getFile();
        InputStream sampleStream = new FileInputStream(fileName);
        PowerWindow window1 = new PowerWindow(sampleStream, 6);
        window1.advanceBy(1194);
        assertTrue(window1.isFull());
        window1.advance();
        assertTrue(window1.isFull());
        window1.advance();
        assertFalse(window1.isFull());
    }

    @Test
    void powerWindowGetWorksAcrossBatches() throws IOException {
        byte[] bytes = bytesForZeroSamples(2);
        var firstBatchSamples = STANDARD_WINDOW_SIZE / 2 - 13;
        var offset = BATCH_SIZE_BYTES - bytesForPowerSamples(firstBatchSamples);
        var sampleBytes = Base64.getDecoder().decode(PowerComputerTest.SAMPLES_BIN_BASE64);
        System.arraycopy(sampleBytes, 0, bytes, offset, sampleBytes.length);
        try (var s = new ByteArrayInputStream(bytes)) {
            var w = new PowerWindow(s, STANDARD_WINDOW_SIZE);
            w.advanceBy(BATCH_SIZE - firstBatchSamples);
            for (int i = 0; i < STANDARD_WINDOW_SIZE; i += 1)
                assertEquals(PowerComputerTest.POWER_SAMPLES[i], w.get(i));
        }
    }
    private static byte[] bytesForZeroSamples(int batchesCount) {
        var bytes = new byte[BATCH_SIZE_BYTES * batchesCount];

        var msbBias = BIAS >> Byte.SIZE;
        var lsbBias = BIAS & ((1 << Byte.SIZE) - 1);
        for (var i = 0; i < bytes.length; i += 2) {
            bytes[i] = (byte) lsbBias;
            bytes[i + 1] = (byte) msbBias;
        }
        return bytes;
    }
    @Test
    public void constructorThrowsExceptionWithIncorrectWindowSize() throws IOException {
        String fileName = (getClass().getResource("/samples.bin")).getFile();
        InputStream sampleStream = new FileInputStream(fileName);
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(sampleStream, 0));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(sampleStream, (int) Math.scalb(1, 16) + 1));
    }

    @Test
    void powerWindowConstructionSuccess() {
        String fileName = (getClass().getResource("/samples.bin")).getFile();
        assertDoesNotThrow(() -> new PowerWindow(new FileInputStream(fileName), 8));
    }

    @Test
    void powerWindowConstructionFailure() {
        String fileName = (getClass().getResource("/samples.bin")).getFile();
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(new FileInputStream(fileName), -1));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(new FileInputStream(fileName), Integer.MAX_VALUE));
    }

    @Test
    void powerWindowIsNotFull() throws Exception {
        byte[] tab = {1, 2, 3, 4};
        InputStream stream = new ByteArrayInputStream(tab);
        PowerWindow window = new PowerWindow(stream, 8);
        assertFalse(window.isFull());
    }

    @Test
    void powerWindowIsFull() throws Exception {
        byte[] tab = new byte[32];
        InputStream stream = new ByteArrayInputStream(tab);
        PowerWindow window = new PowerWindow(stream, 8);
        assertTrue(window.isFull());
    }

}