package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

public class Crc24Test {
    private record AdsbMessageExample(String messageString, String crcString) {
        int getCrc() {
            return Integer.parseInt(crcString(), 16);
        }

        byte[] getMessageOnly() {
            return HexFormat.of().parseHex(messageString());
        }

        byte[] getMessageAndCrc() {
            return HexFormat.of().parseHex(messageString() + crcString());
        }
    }

    @Test
    void crcWorksWithAllExample() {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);

        AdsbMessageExample[] adsbMessageExamples = {
                new AdsbMessageExample("8D392AE499107FB5C00439", "035DB8"),
                new AdsbMessageExample("8D4D2286EA428867291C08", "EE2EC6"),
                new AdsbMessageExample("8D3950C69914B232880436", "BC63D3"),
                new AdsbMessageExample("8D4B17E399893E15C09C21", "9FC014"),
                new AdsbMessageExample("8D4B18F4231445F2DB63A0", "DEEB82"),
                new AdsbMessageExample("8D495293F82300020049B8", "111203"),
                new AdsbMessageExample("", "000000"),
        };

        for (AdsbMessageExample adsbMessageExample : adsbMessageExamples) {
            assertEquals(0, crc24.crc(adsbMessageExample.getMessageAndCrc()));
            assertEquals(adsbMessageExample.getCrc(), crc24.crc(adsbMessageExample.getMessageOnly()));
        }
    }
}
