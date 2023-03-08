package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.InputStream;

public final class SamplesDecoder {
    private final InputStream stream;
    private final int batchSize;

    public SamplesDecoder(InputStream stream, int batchSize) {

        if (stream == null) throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
    }


}
