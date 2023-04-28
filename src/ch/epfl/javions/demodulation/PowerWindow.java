package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is used to compute the power of the radio sample.
 *
 * @author Kevan Lam (356395)
 */
public final class PowerWindow {
    private final PowerComputer powerComputer;
    private boolean ArrayB = false;
    private final static int BATCH_SIZE = 1 << 16;
    private int batch;
    private final int windowSize;
    private long position = 0;
    private final int[] batchA = new int[BATCH_SIZE];
    private final int[] batchB = new int[BATCH_SIZE];

    /**
     * Constructor of the PowerWindow
     *
     * @param stream     the stream of the samples
     * @param windowSize the size of the batch
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize > 0 && windowSize <= BATCH_SIZE);
        this.powerComputer = new PowerComputer(stream, BATCH_SIZE);
        this.windowSize = windowSize;
        batch = powerComputer.readBatch(batchA);
    }

    /**
     * This method returns the size of the window.
     *
     * @return the size of the window
     */
    public int size() {
        return windowSize;
    }

    /**
     * This method returns the position of the window.
     *
     * @return the position of the window
     */
    public long position() {
        return position;
    }

    /**
     * This method checks if the window is full.
     *
     * @return true if the window is full, false otherwise
     */
    public boolean isFull() {
        return position + windowSize <= batch;
    }

    /**
     * This method reads a batch of samples and computes the power of the samples.
     *
     * @param i the index of the sample
     * @return the number of power samples written in the batch
     * @throws IndexOutOfBoundsException if an I/O error occurs
     */
    public int get(int i) {
        if (i < 0 || windowSize <= i) {
            throw new IndexOutOfBoundsException("Index invalide :" + i);
        }

        if ((position % BATCH_SIZE + i) >= BATCH_SIZE) {
            return batchB[(int) (position % BATCH_SIZE + i - BATCH_SIZE)];
        } else {
            return batchA[(int) (position % BATCH_SIZE + i)];
        }
    }

    /**
     * This method reads a batch of samples and computes the power of the samples.
     *
     * @throws IOException if an I/O error occurs
     */
    public void advance() throws IOException {
        ++position;
        if ((position % BATCH_SIZE) + windowSize >= BATCH_SIZE && !ArrayB) {
            batch += powerComputer.readBatch(batchB);
            ArrayB = true;
        } else if (position % BATCH_SIZE == 0 && position != 0) {
            System.arraycopy(batchB, 0, batchA, 0, BATCH_SIZE);
            ArrayB = false;
        }
    }

    /**
     * This method reads a batch of samples and computes the power of the samples.
     *
     * @param offset the offset of the sample
     * @throws IOException if an I/O error occurs
     */
    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument(offset >= 0);
        while (offset > 0) {
            advance();
            --offset;
        }
    }
}

