package ch.epfl.javions.adsb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A RawMessage parser depending on the type code.
 *
 * @author Mateo Tiedra (356525)
 */
public class MessageParser {
    private static final int AIRCRAFT_IDENTIFICATION_MESSAGE_TYPE_CODE_MIN = 1;
    private static final int AIRCRAFT_IDENTIFICATION_MESSAGE_TYPE_CODE_MAX = 4;
    private static final Set AIRBORNE_POSITION_MESSAGE_TYPE_CODES = new HashSet(Arrays.asList(9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22));

    private static final int AIRBORNE_VELOCITY_MESSAGE_TYPE_CODE = 19;


    /**
     * Parse a raw message into a message.
     *
     * @param rawMessage the raw message to parse.
     * @return the parsed message.
     */
    public static Message parse(RawMessage rawMessage) {
        int typeCode = rawMessage.typeCode();
        
        if (typeCode >= AIRCRAFT_IDENTIFICATION_MESSAGE_TYPE_CODE_MIN && typeCode <= AIRCRAFT_IDENTIFICATION_MESSAGE_TYPE_CODE_MAX) {
            return AircraftIdentificationMessage.of(rawMessage);
        } else if (AIRBORNE_POSITION_MESSAGE_TYPE_CODES.contains(typeCode)) {
            return AirbornePositionMessage.of(rawMessage);
        } else if (typeCode == AIRBORNE_VELOCITY_MESSAGE_TYPE_CODE) {
            return AirborneVelocityMessage.of(rawMessage);
        }

        return null;
    }
}
