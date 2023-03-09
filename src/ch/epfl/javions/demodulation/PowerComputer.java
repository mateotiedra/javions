package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {
    private int[] batch;
    private int batchsize;
    private SamplesDecoder samplesDecoder;
    private InputStream inputStream;
    public PowerComputer(InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize > 0 && batchSize % 8 == 0);
        this.batchsize = batchSize;
        this.batch = new int[batchSize];
        this.samplesDecoder = new SamplesDecoder(stream, 2*batchSize);

    }
    public int readBatch(int[] batch) throws IOException{
        Preconditions.checkArgument(batch.length == batchsize);
        short[] shortBatch = new short[2*batchsize];
        int signedBatch = samplesDecoder.readBatch(shortBatch);
    }
}
