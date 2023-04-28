package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is used to demodulate the adsb message.
 *
 * @author Mateo Tiedra (356525)
 */
public final class AdsbDemodulator {
    private final PowerWindow window;

    private static final int WINDOW_SIZE = 1200;
    private static final int TENTH_OF_MICRO_TO_NANO = 100;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        window = new PowerWindow(samplesStream, WINDOW_SIZE);
    }

    /**
     * This method computes the next message in the stream.
     *
     * @return the next message in the stream or null if no message was found.
     * @throws IOException if an I/O error occurs
     */
    public RawMessage nextMessage() throws IOException {
        int lastSp;
        int sp = computeSp(window, 0);
        int nextSp = computeSp(window, 1);

        byte[] messageBytes = new byte[RawMessage.LENGTH];

        RawMessage nextMessage;

        do {
            do {
                window.advance();
                lastSp = sp;
                sp = nextSp;
                nextSp = computeSp(window, 1);

                if (!window.isFull())
                    return null;

            } while (!(sp > lastSp && sp > nextSp && sp >= 2 * computeSv(window)));

            extractMessageBytes(window, messageBytes);
            nextMessage = RawMessage.of(window.position() * TENTH_OF_MICRO_TO_NANO, messageBytes);
        } while (!(nextMessage != null && nextMessage.downLinkFormat() == RawMessage.EXPECTED_FORMAT));

        window.advanceBy(WINDOW_SIZE);

        return nextMessage;
    }

    /**
     * This method extracts the message bytes from the power window (2.3.3).
     *
     * @param window       the power
     * @param messageBytes the array to store the message bytes in
     */
    private void extractMessageBytes(PowerWindow window, byte[] messageBytes) {
        int newByte;
        int sampleIndex;

        for (int i = 0; i < RawMessage.LENGTH; ++i) {
            newByte = 0;
            for (int j = 0; j < Byte.SIZE; ++j) {
                sampleIndex = ((i << 3) + j) * 10;
                int bit = window.get(80 + sampleIndex) < window.get(85 + sampleIndex) ? 0 : 1;
                newByte = newByte | (bit << (7 - j));
            }
            messageBytes[i] = (byte) (newByte);
        }
    }

    /**
     * This method computes the sum p using the first formula (2.3.1).
     *
     * @param window the power window
     * @param offset the offset of the window
     * @return the sum of the power of the samples in the window
     */
    private int computeSp(PowerWindow window, int offset) {
        return window.get(offset) + window.get(offset + 10) + window.get(offset + 35) + window.get(offset + 45);
    }

    /**
     * This method computes the sum v using the second formula (2.3.1).
     *
     * @param window the power window
     * @return the sum of the power of the samples in the window
     */
    private int computeSv(PowerWindow window) {
        return window.get(5) + window.get(15) + window.get(20) + window.get(25) + window.get(30) + window.get(40);
    }

}
