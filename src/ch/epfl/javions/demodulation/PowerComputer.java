package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is used to compute the power of the radio sample.
 *
 * @author Mateo Tiedra (356525)
 */
public final class PowerComputer {
    private final short[] samples;
    private final int batchSize;
    private final SamplesDecoder samplesDecoder;

    private final short[] lastHeightSamples = new short[8];

    /**
     * Constructor of the PowerComputer class.
     *
     * @param stream    the stream of the samples
     * @param batchSize the size of the batch
     */
    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0 && batchSize % 8 == 0);

        this.samples = new short[batchSize * 2];
        this.batchSize = batchSize;
        this.samplesDecoder = new SamplesDecoder(stream, batchSize * 2);
    }

    /**
     * This method reads a batch of samples and computes the power of the samples.
     *
     * @param batch the batch of samples
     * @return the number of power samples written in the batch
     * @throws IOException if an I/O error occurs
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int nbrOfSamplesRead = samplesDecoder.readBatch(samples);

        for (int i = 0; i < nbrOfSamplesRead; i += 2) {
            int lastOfTheLastHeightIndex = i % 8;
            lastHeightSamples[lastOfTheLastHeightIndex] = samples[i];
            lastHeightSamples[lastOfTheLastHeightIndex + 1] = samples[i + 1];
            batch[i / 2] = computePowerSample(lastHeightSamples, lastOfTheLastHeightIndex + 1);
        }
        return nbrOfSamplesRead / 2;
    }

    /**
     * This method computes the power of the samples using the formula given.
     *
     * @param lhs   the last height radio samples
     * @param lotli the index of the last of the last height radio sample
     * @return the power of the samples
     */
    private static int computePowerSample(short[] lhs, int lotli) {
        int InPP = lhs[(lotli - 7 + 8) % 8] - lhs[(lotli - 5 + 8) % 8] + lhs[(lotli - 3 + 8) % 8] - lhs[(lotli - 1 + 8) % 8];
        int QnPP = lhs[(lotli - 6 + 8) % 8] - lhs[(lotli - 4 + 8) % 8] + lhs[(lotli - 2 + 8) % 8] - lhs[lotli % 8];

        return (int) (Math.pow(InPP, 2) + Math.pow(QnPP, 2));
    }
}
