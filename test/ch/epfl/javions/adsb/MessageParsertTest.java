package ch.epfl.javions.adsb;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertNull;

public class MessageParsertTest {
    /*
    @Test
    void testParseAircraftIdentificationMessage() {
        RawMessage rawMessage = new RawMessage();
        Message message = MessageParser.parse(rawMessage);
        assertTrue(message instanceof AircraftIdentificationMessage);
    }

    @Test
    void testParseAirbornePositionMessage() {
        RawMessage rawMessage = new RawMessage();
        Message message = MessageParser.parse(rawMessage);
        assertTrue(message instanceof AirbornePositionMessage);
    }

    @Test
    void testParseAirborneVelocityMessage() {
        RawMessage rawMessage = new RawMessage();
        Message message = MessageParser.parse(rawMessage);
        assertTrue(message instanceof AirborneVelocityMessage);
    }
*/
    @Test
    void testParseInvalidMessage() {
        String message = "0000000000000000000000000000";
        RawMessage rawMessage = RawMessage.of(100, HexFormat.of().parseHex(message));
        Message messageParsed = MessageParser.parse(rawMessage);
        assertNull(messageParsed);
    }
}
