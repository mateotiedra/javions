package ch.epfl.javions;

/**
 * This class implements the CRC-24 algorithm.
 *
 * @author Mateo Tiedra (356525)
 */
final public class Crc24 {
    public static final int GENERATOR = 0xFFF409;
    private static final int CRC_LENGTH = 24;
    private final int[] buildTable;

    /**
     * Constructs a Crc24 object.
     *
     * @param generator the generator used to calculate the CRC values
     */
    public Crc24(int generator) {
        this.buildTable = buildTable(generator);
    }

    /**
     * Returns the CRC of the message.
     *
     * @param bytes the bytes on which to compute the CRC, the message.
     * @return the CRC of the message
     */
    public int crc(byte[] bytes) {
        int crc = 0;

        for (byte octets : bytes) {
            crc = ((crc << 8) | Byte.toUnsignedInt(octets)) ^ buildTable[filterNLeastSignificantBits(crc >>> (CRC_LENGTH - 8), 8)];
        }

        for (int i = 0; i < 3; ++i) {
            crc = (crc << 8) ^ buildTable[(crc >>> (CRC_LENGTH - 8)) & 0xFF];
        }

        return filterNLeastSignificantBits(crc, CRC_LENGTH);
    }

    /**
     * Implements the CRC 24 algorithm bitwise.
     *
     * @param bytes the bytes on which to compute the CRC, the message.
     * @return the CRC of the message
     */
    private static int crc_bitwise(byte[] bytes, int generator) {
        int crc = 0;
        int[] table = {0, generator};

        for (byte octets : bytes) {
            for (int i = 0; i < 8; ++i) {
                short bit = (short) ((octets >> (7 - i)) & 1);
                crc = ((crc << 1) | bit) ^ table[getNthBit(crc, CRC_LENGTH - 1)];
            }
        }

        for (int i = 0; i < CRC_LENGTH; ++i) {
            crc = (crc << 1) ^ table[getNthBit(crc, CRC_LENGTH - 1)];
        }

        return filterNLeastSignificantBits(crc, CRC_LENGTH);
    }

    /**
     * This method builds a table for computing CRC values for the 256 octets.
     *
     * @param generator the generator used to calculate the CRC values
     * @return an integer array the table
     */
    private static int[] buildTable(int generator) {
        int[] table = new int[256];
        for (int i = 0; i < 256; ++i) {
            table[i] = crc_bitwise(new byte[]{(byte) i}, generator);
        }
        return table;
    }

    /**
     * This method returns the nth bit of a value.
     *
     * @param value the value
     * @param index the index of the bit
     * @return the nth bit of the value
     */
    private static int getNthBit(int value, int index) {
        return Bits.testBit(value, index) ? 1 : 0;
    }

    /**
     * This method returns the n least significant bits of a value.
     *
     * @param value the value
     * @param n     the number of bits
     * @return the n least significant bits of the value
     */
    private static int filterNLeastSignificantBits(int value, int n) {
        return value & ((1 << n) - 1);
    }
}
