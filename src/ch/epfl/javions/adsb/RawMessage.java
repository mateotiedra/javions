package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Represents a raw ADS-B message.
 *
 * @param timeStampNs the time stamp of the message
 * @param bytes       the bytes of the message
 * @author Mateo Tiedra (356525)
 */
public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int LENGTH = 14;

    private static final int DF_POS_IN_FIRST_BYTE = 3;
    public static final int EXPECTED_FORMAT = 17;

    private static final int TYPE_CODE_POS_IN_PAYLOAD = 51;

    private static final int FIVE_BITS_MASK = 0b11111;

    public RawMessage {
        Preconditions.checkArgument(timeStampNs >= 0 && bytes.size() == LENGTH);
    }

    /**
     * Returns a RawMessage object if the message is valid (depending on the Crc24).
     *
     * @param timeStampNs the time stamp of the message
     * @param bytes       the bytes of the message
     * @return a RawMessage object if the message is valid, null otherwise
     */
    public static RawMessage of(long timeStampNs, byte[] bytes) {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        if (crc24.crc(bytes) == 0) {
            return new RawMessage(timeStampNs, new ByteString(bytes));
        }
        return null;
    }

    /**
     * Returns the size of the message.
     *
     * @param byte0 the first byte of the message
     * @return the size of the message
     */
    public static int size(byte byte0) {
        return ((Byte.toUnsignedInt(byte0) >>> DF_POS_IN_FIRST_BYTE) == EXPECTED_FORMAT) ? LENGTH : 0;
    }

    /**
     * Extracts the type code from a ME attribute.
     *
     * @param payload the ME attribute
     * @return the type code extracted from the payload
     */
    public static int typeCode(long payload) {
        return (int) ((payload >>> TYPE_CODE_POS_IN_PAYLOAD) & FIVE_BITS_MASK);
    }

    /**
     * Returns the DF attribute of the message.
     *
     * @return the DF attribute of the message
     */
    public int downLinkFormat() {
        return bytes.byteAt(0) >>> DF_POS_IN_FIRST_BYTE;
    }

    /**
     * Returns the ICAO address of the message.
     *
     * @return the ICAO address of the message
     */
    public IcaoAddress icaoAddress() {
        return new IcaoAddress(bytes.toString().substring(2, 8));
    }

    /**
     * Returns the ME attribute of the message.
     *
     * @return the ME attribute of the message
     */
    public long payload() {
        return bytes.bytesInRange(4, 11);
    }

    /**
     * Returns the type code of the message.
     *
     * @return the type code of the message
     */
    public int typeCode() {
        return typeCode(payload());
    }
}
