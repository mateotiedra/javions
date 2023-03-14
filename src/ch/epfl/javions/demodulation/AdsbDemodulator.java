package ch.epfl.javions.demodulation;

import ch.epfl.javions.ByteString;
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

    AdsbDemodulator(InputStream samplesStream) throws IOException {
        window = new PowerWindow(samplesStream, 1200);
    }

    /**
     * This method computes the next message in the stream.
     *
     * @return the next message in the stream or null if no message was found.
     * @throws IOException if an I/O error occurs
     */
    public RawMessage nextMessage() throws IOException {
        int lastSp;
        int sp = computeSp(window);
        int nextSp = computeSp(window, 1);
        int sv;

        RawMessage nextMessage = null;

        do {
            do {
                lastSp = sp;
                sp = nextSp;
                nextSp = computeSp(window, 1);

                window.advance();
                if (!window.isFull())
                    return null;
            } while (!(sp > lastSp && sp > nextSp && sp >= 2 * computeSv(window)));

            ByteString bytes = new ByteString(extractMessageBytes(window));
            nextMessage = new RawMessage(window.position() * 100, bytes);
        } while (nextMessage.downLinkFormat() != 17);

        return nextMessage;
    }

    /**
     * This method extracts the message bytes from the power window (2.3.3).
     *
     * @param window the power window
     * @return the bytes containing the message
     */
    private static byte[] extractMessageBytes(PowerWindow window) {
        byte[] messageBytes = new byte[RawMessage.LENGTH];
        for (int i = 0; i < RawMessage.LENGTH; i++) {
            byte newByte = 0;
            for (int j = 0; j < Byte.SIZE; j++) {
                int bitIndex = i * 8 + j;
                int bit = window.get(80 + 10 * bitIndex) > window.get(85 + 10 * bitIndex) ? 1 : 0;
                newByte |= bit << j;
            }
            messageBytes[i] = newByte;
        }

        return messageBytes;
    }

    /**
     * This method computes the sum p using the first formula (2.3.1).
     *
     * @param window the power window
     * @param offset the offset of the window
     * @return the sum of the power of the samples in the window
     */
    private static int computeSp(PowerWindow window, int offset) {
        return window.get(offset) + window.get(offset + 10) + window.get(offset + 35) + window.get(offset + 45);
    }

    /**
     * This method computes the sum p using the first formula (2.3.1).
     *
     * @param window the power window
     * @return the sum of the power of the samples in the window
     */
    private static int computeSp(PowerWindow window) {
        return computeSp(window, 0);
    }

    /**
     * This method computes the sum v using the second formula (2.3.1).
     *
     * @param window the power window
     * @return the sum of the power of the samples in the window
     */
    private static int computeSv(PowerWindow window) {
        return window.get(5) + window.get(15) + window.get(20) + window.get(25) + window.get(30) + window.get(40);
    }
}
