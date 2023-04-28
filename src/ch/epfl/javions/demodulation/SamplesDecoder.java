package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A class that decodes the samples read from an input stream.
 *
 * @author Mateo Tiedra (
 */
public final class SamplesDecoder {
    private final InputStream stream;
    private final int batchSize;
    private final byte[] samplesTable;

    private static final int RECENTERING_NUMBER = 2048;

    private static final int TWELVE_BITS_MASK = 0b111111111111;

    /**
     * Constructs a new SamplesDecoder that will read from the given stream and
     * decode the samples in batches of the given size.
     *
     * @param stream    the stream to read from
     * @param batchSize the size of the batches to read
     * @throws NullPointerException     if the given stream is null
     * @throws IllegalArgumentException if the given batch size is not positive
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0);

        this.stream = Objects.requireNonNull(stream);
        this.batchSize = batchSize;
        this.samplesTable = new byte[batchSize * 2];
    }

    /**
     * Returns the next batch of samples read from the stream.
     *
     * @param batch the array to store the samples in
     * @return the number of samples read
     * @throws NullPointerException     if the given array is null
     * @throws IllegalArgumentException if the given array size is not equal to
     *                                  the batch size
     * @throws IOException              if an error occurs while reading from the stream
     */
    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int bytesRead = stream.readNBytes(samplesTable, 0, batchSize * 2);

        for (int i = 0; i < bytesRead; i += 2) {
            int msb = Byte.toUnsignedInt(samplesTable[i + 1]) << Byte.SIZE;
            int lsb = Byte.toUnsignedInt(samplesTable[i]);

            batch[i / 2] = (short) (((msb | lsb) & TWELVE_BITS_MASK) - RECENTERING_NUMBER);
        }

        return bytesRead / 2;
    }
}
