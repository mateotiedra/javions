package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class SamplesDecoder {
    private final InputStream stream;

    private final int batchSize;
    private final byte[] samplesTable;

    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0);

        this.stream = Objects.requireNonNull(stream);
        this.batchSize = batchSize;
        this.samplesTable = new byte[batchSize * 2];
    }

    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);

        int bytesRead = stream.readNBytes(samplesTable, 0, batchSize);

        for (int i = 0; i < samplesTable.length; i += 2) {
            batch[i] = (short) ((samplesTable[i + 1] << 8) | samplesTable[i]);
        }

        return bytesRead / 2;
    }
}
