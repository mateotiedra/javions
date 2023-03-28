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
        Preconditions.checkArgument(batchSize > 0 && batchSize % Byte.SIZE == 0);

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
            int lastOfTheLastHeightIndex = i % Byte.SIZE;
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
        int InPP = lhs[(lotli - 7 + Byte.SIZE) % Byte.SIZE] - lhs[(lotli - 5 + Byte.SIZE) % Byte.SIZE] + lhs[(lotli - 3 + Byte.SIZE) % Byte.SIZE] - lhs[(lotli - 1 + Byte.SIZE) % Byte.SIZE];
        int QnPP = lhs[(lotli - 6 + Byte.SIZE) % Byte.SIZE] - lhs[(lotli - 4 + Byte.SIZE) % Byte.SIZE] + lhs[(lotli - 2 + Byte.SIZE) % Byte.SIZE] - lhs[lotli % Byte.SIZE];

        return (int) (Math.pow(InPP, 2) + Math.pow(QnPP, 2));
    }
}
