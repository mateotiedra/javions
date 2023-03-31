package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AircraftStateAccumulatorTest {
    @Test
    public void test1() throws IOException {
        String f = "resources/samples_20230304_1442.bin";
        IcaoAddress expectedAddress = new IcaoAddress("4D2228");
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            AircraftStateAccumulator<AircraftState> a =
                    new AircraftStateAccumulator<>(new AircraftState());
            while ((m = d.nextMessage()) != null) {
                if (!m.icaoAddress().equals(expectedAddress)) continue;
                Message pm = MessageParser.parse(m);
                if (pm != null) a.update(pm);
            }
        }
    }

    class AircraftState implements AircraftStateSetter {
        @Override
        public void setLastMessageTimeStampNs(long timeStampNs) {
            System.out.println("timestamp : " + timeStampNs);
        }

        @Override
        public void setCategory(int category) {
            System.out.println("category : " + category);
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
