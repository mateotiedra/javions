package ch.epfl.javions;

import java.util.HexFormat;

import static java.util.Objects.checkFromToIndex;

/**
 * Utility class for bit manipulation.
 *
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 */
public class ByteString {
    byte[] bytes;

    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Returns a new ByteString from the given hexadecimal string.
     *
     * @param hexString the hexadecimal string to convert
     * @return a new ByteString from the given hexadecimal string
     * @throws IllegalArgumentException if the given string is not a valid
     *                                  hexadecimal string of if the given string is empty
     */
    public ByteString ofHexadecimalString(String hexString) {
        Preconditions.checkArgument(hexString != null && hexString.length() > 0 && hexString.length() % 2 == 0);

        String regex = "^(0x|0X)?[0-9a-fA-F]+$";
        Preconditions.checkArgument(hexString.matches(regex));

        HexFormat hf = HexFormat.of().withUpperCase();
        return new ByteString(hf.parseHex(hexString));
    }

    /**
     * Returns the number of bytes in this ByteString.
     *
     * @return the number of bytes in this ByteString
     */
    public int size() {
        return bytes.length;
    }

    /**
     * Returns the byte value at the specified index.
     *
     * @param index the index of the byte to return
     * @return the byte value at the specified index
     * @throws IndexOutOfBoundsException if the index is negative or greater than
     *                                   or equal to the size of this ByteString
     */
    public int byteAt(int index) {
        if (index < 0 || index >= bytes.length)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);

        return bytes[index];
    }

    /**
     * Extracts a long value from a byte array.
     *
     * @param fromIndex the index of the first byte to extract
     * @param toIndex   the index of the last byte to extract
     * @return the long value extracted from the byte array
     * @throws IndexOutOfBoundsException if the indices are out of bounds
     * @throws IllegalArgumentException  if the indices are not in ascending order
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        // Validate input indices
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > bytes.length) {
            throw new IndexOutOfBoundsException();
        } else if (toIndex - fromIndex > 8) {
            throw new IllegalArgumentException("Cannot extract more than 8 bytes");
        }

        checkFromToIndex(fromIndex, toIndex, bytes.length);

        // Extract bytes as a long value
        long value = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            value = (value << 8) + (bytes[i] & 0xFF);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteString that = (ByteString) o;

        return java.util.Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(bytes);
    }

    /**
     * Returns a string representation of this ByteString.
     *
     * @return a string representation of this ByteString
     */
    @Override
    public String toString() {

        HexFormat hf = HexFormat.of().withUpperCase();
        return hf.formatHex(bytes);
    }
}
