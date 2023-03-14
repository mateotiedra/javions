package ch.epfl.javions.demodulation;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HexFormat;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdsbDemodulatorVinZTest {
    public static String fileName = Objects.requireNonNull(AdsbDemodulatorVinZTest.class.getResource("/samples_20230304_1442.bin")).getFile();
    private static final HexFormat hf = HexFormat.of().withUpperCase();

    @Test
    public static void main(String[] args) throws IOException {
        try (InputStream s = new FileInputStream(fileName)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            int i = 0;

            double start = System.currentTimeMillis() / 1000.0;
            while ((m = d.nextMessage()) != null) {
                //System.out.printf("msg %d : ", ++i);
                //System.out.println(m);
            }
            double end = System.currentTimeMillis() / 1000.0;
            System.out.printf("Executed in %.3f seconds\n", end - start);
        }
    }

    @Test
    void fiveFirstAreGood() throws IOException {
        try (InputStream s = new FileInputStream(fileName)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            int i = 0;

            long[] timeStamps = new long[]{8096200, 75898000, 100775400, 116538700, 129268900};
            byte[][] bytes = new byte[][]{hf.parseHex("8D4B17E5F8210002004BB8B1F1AC"), hf.parseHex("8D49529958B302E6E15FA352306B"), hf.parseHex("8D39D300990CE72C70089058AD77"), hf.parseHex("8D4241A9601B32DA4367C4C3965E"), hf.parseHex("8D4B1A00EA0DC89E8F7C0857D5F5")};

            while ((m = d.nextMessage()) != null && i < 5) {
                assertEquals(m.timeStampNs(), timeStamps[i]);
                assertEquals(m.bytes(), new ByteString(bytes[i]));
                ++i;
            }
        }
    }
}
