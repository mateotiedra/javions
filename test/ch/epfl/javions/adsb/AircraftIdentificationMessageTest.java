package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftIdentificationMessageTest {
    @Test
    void aircraftIdentificationConstructorNullPointer() {
        assertThrows(NullPointerException.class, () -> new AircraftIdentificationMessage(1499146900, new IcaoAddress("4D2228"), 163, null));
        assertThrows(NullPointerException.class, () -> new AircraftIdentificationMessage(1499146900, null, 163, new CallSign("RYR7JD")));
    }

    @Test
    void aircraftIdentificationConstructorIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftIdentificationMessage(-1, new IcaoAddress("4D2228"), 163, new CallSign("RYR7JD")));
    }

    @Test
    void aircraftIdentificationConstructor() {
        AircraftIdentificationMessage aircraftIdentificationMessage = new AircraftIdentificationMessage(1499146900, new IcaoAddress("4D2228"), 163, new CallSign("RYR7JD"));
        assertEquals(1499146900, aircraftIdentificationMessage.timeStampNs());
        assertEquals(new IcaoAddress("4D2228"), aircraftIdentificationMessage.icaoAddress());
        assertEquals(163, aircraftIdentificationMessage.category());
        assertEquals(new CallSign("RYR7JD"), aircraftIdentificationMessage.callSign());
    }

    @Test
    void aircraftIdentificationFiveExampleWorks() {
        String url = Objects.requireNonNull(getClass().getResource("/samples_20230304_1442.bin")).getFile();
        try (InputStream s = new FileInputStream(url)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;

            AircraftIdentificationMessage[] expectedAircraftIdentificationMessage = {
                    new AircraftIdentificationMessage(1499146900L, new IcaoAddress("4D2228"), 163, new CallSign("RYR7JD")),
                    new AircraftIdentificationMessage(2240535600L, new IcaoAddress("01024C"), 163, new CallSign("MSC3361")),
                    new AircraftIdentificationMessage(2698727800L, new IcaoAddress("495299"), 163, new CallSign("TAP931")),
                    new AircraftIdentificationMessage(3215880100L, new IcaoAddress("A4F239"), 165, new CallSign("DAL153")),
                    new AircraftIdentificationMessage(4103219900L, new IcaoAddress("4B2964"), 161, new CallSign("HBPRO")),
            };


            int aimCounter = 0;
            while ((m = d.nextMessage()) != null) {
                if (1 <= m.typeCode() && m.typeCode() <= 4 && aimCounter < expectedAircraftIdentificationMessage.length) {
                    assertEquals(expectedAircraftIdentificationMessage[aimCounter], AircraftIdentificationMessage.of(m));
                    aimCounter++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
