package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

public class RawMessageTest {

    private static final HexFormat hf = HexFormat.of().withUpperCase();

    RawMessage[] rawMessagesTest = new RawMessage[]{
            RawMessage.of(8096200, hf.parseHex("8D4B17E5F8210002004BB8B1F1AC")),
            RawMessage.of(75898000, hf.parseHex("8D49529958B302E6E15FA352306B")),
            RawMessage.of(100775400, hf.parseHex("8D39D300990CE72C70089058AD77")),
            RawMessage.of(116538700, hf.parseHex("8D4241A9601B32DA4367C4C3965E")),
            RawMessage.of(129268900, hf.parseHex("8D4B1A00EA0DC89E8F7C0857D5F5")),
    };

    @Test
    void ofWorksWithCorrectMessage() {
        assertNotNull(RawMessage.of(8096200, hf.parseHex("8D4B17E5F8210002004BB8B1F1AC")));
    }

    @Test
    void ofWorksWithIncorrectTimeStampNegative() {
        assertThrows(IllegalArgumentException.class, () -> RawMessage.of(-1, hf.parseHex("8D4B17E5F8210002004BB8B1F1AC")));
    }

    @Test
    void ofWorksWithIncorrectHexString() {
        assertThrows(IllegalArgumentException.class, () -> RawMessage.of(10, hf.parseHex("8D4B17E5F82100002004BB8B1F1AC")));
    }

    @Test
    void sizeWorksWithAdsbMessage() {
        assertEquals(14, RawMessage.size(hf.parseHex("8D4B17E5F8210002004BB8B1F1AC")[0]));
    }

    @Test
    void sizeWorksWithOtherFormatMessage() {
        assertEquals(0, RawMessage.size((byte) (hf.parseHex("8D4B17E5F8210002004BB8B1F1AC")[0] + 0b10000000)));
    }

    @Test
    void typeCodeWorksWithCorrectME() {
        byte[] testME = Arrays.copyOfRange(hf.parseHex("8D4B17E5F8210002004BB8B1F1AC"), 4, 11);
        byte[] testME2 = Arrays.copyOfRange(hf.parseHex("8D39D300990CE72C70089058AD77"), 4, 11);

        long testMELong = 0;
        long testMELong2 = 0;
        for (byte b : testME) {
            testMELong = (testMELong << 8) | (b & 0xff);
        }

        for (byte b : testME2) {
            testMELong2 = (testMELong2 << 8) | (b & 0xff);
        }

        assertEquals(0b11111, RawMessage.typeCode(testMELong));
        assertEquals(0b10011, RawMessage.typeCode(testMELong2));
    }

    @Test
    void downLinkFormatWorksCorrectly() {
        for (RawMessage rawMessageTest : rawMessagesTest) {
            assertEquals(17, rawMessageTest.downLinkFormat());
        }
    }

    @Test
    void icaoAddressWorksCorrectly() {
        IcaoAddress[] expectedValues = new IcaoAddress[]{
                new IcaoAddress("4B17E5"),
                new IcaoAddress("495299"),
        };

        for (int i = 0; i < 2; i++) {
            assertEquals(expectedValues[i], rawMessagesTest[i].icaoAddress());
        }
    }

    @Test
    void payloadWorksCorrectly() {
        byte[] testME = Arrays.copyOfRange(hf.parseHex("8D4B17E5F8210002004BB8B1F1AC"), 4, 11);
        byte[] testME2 = Arrays.copyOfRange(hf.parseHex("8D39D300990CE72C70089058AD77"), 4, 11);

        long testMELong = 0;
        long testMELong2 = 0;
        for (byte b : testME) {
            testMELong = (testMELong << 8) | (b & 0xff);
        }

        for (byte b : testME2) {
            testMELong2 = (testMELong2 << 8) | (b & 0xff);
        }

        long[] expectedValues = new long[]{
                testMELong,
                testMELong2,
        };

        assertEquals(expectedValues[0], rawMessagesTest[0].payload());
        assertEquals(expectedValues[1], rawMessagesTest[2].payload());
    }

    @Test
    void nonStaticTypeCodeWorks() {
        assertEquals(0b11111, rawMessagesTest[0].typeCode());
        assertEquals(0b10011, rawMessagesTest[2].typeCode());
    }
}
