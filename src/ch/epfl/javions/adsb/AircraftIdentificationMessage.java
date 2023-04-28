package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Represents an Aircraft Identification Message.
 *
 * @author Mateo Tiedra (356525)
 */
public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {

    private static final String REPRESENTATION_TABLE = "*ABCDEFGHIJKLMNOPQRSTUVWXYZ***** ***************0123456789";

    private static final int ENCODED_CHAR_SIZE = 6;

    /**
     * Constructs an AircraftIdentificationMessage.
     *
     * @param timeStampNs the time stamp of the message (in nanoseconds)
     * @param icaoAddress the ICAO address of the aircraft
     * @param category    the category of the aircraft
     * @param callSign    the call sign of the aircraft
     */
    public AircraftIdentificationMessage {
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    /**
     * Returns an AircraftIdentificationMessage if the given raw message contains a valid call sign.
     *
     * @param rawMessage the raw message to be parsed
     * @return an AircraftIdentificationMessage if the given raw message  contains a valid call sign, null otherwise
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        CallSign callSign = getCallSign(rawMessage.payload());
        if (callSign != null)
            return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), getCategory(rawMessage), callSign);

        return null;
    }

    /**
     * Returns the category of the aircraft.
     *
     * @param rawMessage : the raw message from which the category is extracted
     * @return the category of the aircraft
     */
    private static int getCategory(RawMessage rawMessage) {
        int CA = (int) ((rawMessage.payload() >>> 48) & 0b111);
        return (((14 - rawMessage.typeCode()) << 4) & 0b11110000) | CA;
    }

    /**
     * Returns the call sign of the aircraft.
     *
     * @param payload : the payload of the raw message from which the call sign is extracted
     * @return the call sign of the aircraft
     */
    private static CallSign getCallSign(long payload) {
        StringBuilder callSignString = new StringBuilder();
        boolean firstLetterOk;
        for (int i = 0; i < Byte.SIZE; ++i) {
            int charIndex = Bits.extractUInt(payload, ENCODED_CHAR_SIZE * i, ENCODED_CHAR_SIZE);
            String newChar = charIndex >= REPRESENTATION_TABLE.length() ? "*" : String.valueOf(REPRESENTATION_TABLE.charAt(charIndex));
            if (!newChar.equals("*")) {
                firstLetterOk = !newChar.equals(" ");
                callSignString.append(firstLetterOk ? newChar : "");
            } else {
                return null;
            }
        }
        return new CallSign(callSignString.reverse().toString());
    }
}
