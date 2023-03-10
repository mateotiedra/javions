package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;


class PowerWindowTest {
    @Test
    public void windowOK() throws IOException {
        String fileName = (getClass().getResource("/samples.bin")).getFile();
        InputStream sampleStream = new FileInputStream(fileName);
        PowerWindow window = new PowerWindow(sampleStream, 6);
        assertEquals(6, window.size());
        assertEquals(73, window.get(0));
        assertThrows(IllegalArgumentException.class, () -> window.get(7));
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