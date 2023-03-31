package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Units;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class AirborneVelocityMessageTest {
    public static String fileName = Objects.requireNonNull(AirborneVelocityMessageTest.class.getResource("/samples_20230304_1442.bin")).getFile();

    @Test
    public void testAllMessages() throws IOException {
        try (InputStream s = new FileInputStream(fileName)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;

            String rightValues = "AirborneVelocityMessage[timeStampNs=100775400, icaoAddress=IcaoAddress[string=39D300], speed=217.1759987875795, trackOrHeading=5.707008696317668]\n" +
                    "AirborneVelocityMessage[timeStampNs=146689300, icaoAddress=IcaoAddress[string=440237], speed=227.75426436901594, trackOrHeading=4.1068443167797195]\n" +
                    "AirborneVelocityMessage[timeStampNs=208341000, icaoAddress=IcaoAddress[string=4D029F], speed=161.15254486753832, trackOrHeading=3.9337627224977503]\n" +
                    "AirborneVelocityMessage[timeStampNs=210521800, icaoAddress=IcaoAddress[string=01024C], speed=228.01904908511267, trackOrHeading=5.311655187675027]\n" +
                    "AirborneVelocityMessage[timeStampNs=232125000, icaoAddress=IcaoAddress[string=4B17E5], speed=114.64264880353804, trackOrHeading=5.335246702497837]\n";

            String finalValues = "";
            int i = 0;
            while ((m = d.nextMessage()) != null && i < 5) {
                int typeCode = m.typeCode();
                if (typeCode == 19) {
                    AirborneVelocityMessage a = AirborneVelocityMessage.of(m);
                    if (a != null) {
                        //System.out.println(a);
                        finalValues += a + "\n";
                        ++i;
                    }
                }
            }
            assertEquals(rightValues, finalValues);
        }
    }

    @Test
    public void testSubType3Message() {
        RawMessage B = new RawMessage(0, ByteString.ofHexadecimalString("8DA05F219B06B6AF189400CBC33F"));
        AirborneVelocityMessage a = AirborneVelocityMessage.of(B);
        System.out.println(a);
        assertEquals(375, Units.convertTo(a.speed(), Units.Speed.KNOT));
        assertEquals(4.25833066717054, a.trackOrHeading());
    }

    @Test
    public void testEdMessage() {
        //#877
        RawMessage B = new RawMessage(0, ByteString.ofHexadecimalString("8D485020994409940838175B284F"));
        AirborneVelocityMessage a = AirborneVelocityMessage.of(B);
        String correctMessage = "AirborneVelocityMessage[timeStampNs=0, icaoAddress=IcaoAddress[string=485020], speed=81.90013721178154, trackOrHeading=3.1918647255875205]";
        assertEquals(correctMessage, a.toString());
    }
}