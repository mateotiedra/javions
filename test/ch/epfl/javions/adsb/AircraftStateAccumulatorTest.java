package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftStateAccumulatorTest {
    @Test
    void testConscrutor() {
        assertThrows(NullPointerException.class, () -> new AircraftStateAccumulator<>(null));
    }

    @Test
    public void testAccumulator() throws IOException {
        String f = "resources/samples_20230304_1442.bin";
        IcaoAddress expectedAddress = new IcaoAddress("3C6481");
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            AircraftStateAccumulator<AircraftState> a = new AircraftStateAccumulator<>(new AircraftState());

            while ((m = d.nextMessage()) != null) {
                if (!m.icaoAddress().equals(expectedAddress)) continue;
                Message pm = MessageParser.parse(m);
                if (pm != null) a.update(pm);
            }
        }
        String test1 = "8D39D300990CE72C70089058AD77";
        byte[] bytes = hexStringToByteArray(test1);
        RawMessage message = RawMessage.of(0, bytes);

        Message parsedMessage = MessageParser.parse(message);

        AircraftStateAccumulator<AircraftState> accumulator = new AircraftStateAccumulator<>(new AircraftState());
        if (parsedMessage != null) {
            accumulator.update(parsedMessage);
        }

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    class AircraftState implements AircraftStateSetter {
        @Override
        public void setLastMessageTimeStampNs(long timeStampNs) {
            //System.out.println("timestamp : " + timeStampNs);
        }

        @Override
        public void setCategory(int category) {
            //System.out.println("category : " + category);
        }

        @Override
        public void setCallSign(CallSign callSign) {
            System.out.println("indicatif : " + callSign);
        }

        @Override
        public void setAltitude(double altitude) {
            System.out.println("altitude : " + altitude);
        }

        @Override
        public void setVelocity(double velocity) {
            System.out.println("velocity : " + velocity);
        }

        @Override
        public void setTrackOrHeading(double trackOrHeading) {
            System.out.println("track or heading : " + trackOrHeading);
        }

        @Override
        public void setPosition(GeoPos position) {
            System.out.println("position : " + position);
        }
    }
}
