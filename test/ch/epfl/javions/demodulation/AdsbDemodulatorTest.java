package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdsbDemodulatorTest {
    @Test
    void testFiveFirstMessageOfSamples() throws IOException {
        String url = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try (InputStream s = new FileInputStream(url)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;

            String[] expectedMessagesString = new String[]{
                    "RawMessage[timeStampNs=8096200, bytes=8D4B17E5F8210002004BB8B1F1AC]",
                    "RawMessage[timeStampNs=75898000, bytes=8D49529958B302E6E15FA352306B]",
                    "RawMessage[timeStampNs=100775400, bytes=8D39D300990CE72C70089058AD77]",
                    "RawMessage[timeStampNs=116538700, bytes=8D4241A9601B32DA4367C4C3965E]",
                    "RawMessage[timeStampNs=129268900, bytes=8D4B1A00EA0DC89E8F7C0857D5F5]",
            };

            for (int i = 0; i < 5; i++) {
                m = d.nextMessage();
                //System.out.println(m.downLinkFormat());
                assertEquals(expectedMessagesString[i], m.toString());
            }
            /*while ((m = d.nextMessage()) != null)
                System.out.println(m);*/
        }
    }

    @Test
    void readCorrectNumberOfMessageInSample() throws IOException {
        String url = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try (InputStream s = new FileInputStream(url)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            int messageCounter = 0;

            while ((m = d.nextMessage()) != null) {
                ++messageCounter;
            }
            assertEquals(384, messageCounter);
        }
    }

    @Test
    void readMessageFastEnough() throws IOException {
        String url = getClass().getResource("/samples_20230304_1442.bin").getFile();
        try (InputStream s = new FileInputStream(url)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            int messageCounter = 0;

            double start = System.currentTimeMillis() / 1000.0;

            while ((m = d.nextMessage()) != null) {
                System.out.println("Message %d : %s".formatted(++messageCounter, m));
            }

            double end = System.currentTimeMillis() / 1000.0;
            System.out.println("Executed in %.3f seconds".formatted(end - start));

            assertTrue(end - start < 7.5);
        }

    }

}
