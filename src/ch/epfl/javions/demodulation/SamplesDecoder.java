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
        int bytesRead = stream.readNBytes(samplesTable, 0, batchSize * 2);

        for (int i = 0; i < samplesTable.length; i += 2) {
            int msb = Byte.toUnsignedInt(samplesTable[i + 1]) << 8;
            int lsb = Byte.toUnsignedInt(samplesTable[i]);

            batch[i / 2] = (short) (((msb | lsb) & 0x0FFF) - 2048);
        }

        return bytesRead / 2;
    }
}
