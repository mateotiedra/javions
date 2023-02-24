package ch.epfl.javions;

import static java.util.Objects.checkFromIndexSize;
import static java.util.Objects.checkIndex;

/**
 * Utility class for bit manipulation.
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 */
public class Bits {
    private Bits() {}
    /**
     * Returns the unsigned integer value represented by the specified number of
     * bits in the specified value, starting at the specified index.
     *
     * @param value the value to extract the unsigned integer from
     * @param start the index of the first bit to extract
     * @param size the number of bits to extract
     * @return the unsigned integer value represented by the specified number of
     *         bits in the specified value, starting at the specified index
     * @throws IllegalArgumentException if {@code size} is negative or greater
     *         than {@code Integer.SIZE}
     * @throws IndexOutOfBoundsException if {@code start} is negative or greater
     *         than or equal to {@code Long.SIZE}, or if {@code start + size} is
     *         negative or greater than {@code Long.SIZE}
     */
    public int extractUInt(long value, int start, int size) throws IllegalArgumentException,IndexOutOfBoundsException{
        checkIndex(start, Integer.SIZE);
        checkFromIndexSize(start, size, Long.SIZE);

        return (int) ((value >>> start) & (1L << size) - 1);
    }
    /**
     * Returns the bit value at the specified index.
     *
     * @param value the value to extract the bit from
     * @param index the index of the bit to extract
     * @return the bit value at the specified index
     * @throws IndexOutOfBoundsException if the index is negative or greater than
     *         or equal to {@code Long.SIZE}
     */
    public static boolean testBit(long value, int index) {
        checkIndex(index, Long.SIZE);
        return ((value >>> index) & 1) == 1;
    }
}
