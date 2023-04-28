package ch.epfl.javions;

import static java.util.Objects.checkFromIndexSize;
import static java.util.Objects.checkIndex;

/**
 * Utility class for bit manipulation.
 *
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 */
public final class Bits {
    private Bits() {
    }

    /**
     * Returns the unsigned integer value represented by the specified number of
     * bits in the specified value, starting at the specified index.
     *
     * @param value the value to extract the unsigned integer from
     * @param start the index of the first bit to extract
     * @param size  the number of bits to extract
     * @return the unsigned integer value represented by the specified number of
     * bits in the specified value, starting at the specified index
     * @throws IllegalArgumentException  if size is negative or greater
     *                                   than Integer.SIZE
     * @throws IndexOutOfBoundsException if start is negative or greater
     *                                   than or equal to Long.SIZE, or if (start + size) is
     *                                   negative or greater than Long.SIZE.
     */
    public static int extractUInt(long value, int start, int size) {
        Preconditions.checkArgument(size > 0 && size < Integer.SIZE);
        checkFromIndexSize(start, size, Long.SIZE);

        return (int) ((value >>> start) & (1L << size) - 1);
    }

    /**
     * Returns the bit value at the specified index.
     *
     * @param value the value to extract the bit from.
     * @param index the index of the bit to extract.
     * @return true if the bit at the specified index is equal to 1.
     * @throws IndexOutOfBoundsException if the index is negative or greater than
     *                                   or equal to Long.SIZE
     */
    public static boolean testBit(long value, int index) {
        checkIndex(index, Long.SIZE);
        return ((value >>> index) & 1) == 1;
    }
}
