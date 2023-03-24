package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {

    private static final String[] REPRESENTATION_TABLE = buildRepresentationTable();

    public AircraftIdentificationMessage {
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        CallSign callSign = getCallSign(rawMessage.payload());
        if (callSign != null)
            return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), getCategory(rawMessage), callSign);

        return null;
    }

    private static int getCategory(RawMessage rawMessage) {
        int CA = (int) ((rawMessage.payload() >>> 48) & 0b111);
        return (((14 - rawMessage.typeCode()) << 4) & 0b11110000) | CA;
    }

    private static CallSign getCallSign(long payload) {
        String callSignString = "";
        for (int i = 0; i < 8; ++i) {
            int charIndex = Bits.extractUInt(payload, 6 * i, 6);
            String newChar = REPRESENTATION_TABLE[charIndex];
            if (newChar != null) {
                callSignString = newChar + callSignString;
            } else {
                return null;
            }
        }
        return new CallSign(callSignString.trim());
    }

    private static String[] buildRepresentationTable() {
        List<String> table = new ArrayList<>();

        table.add(null);

        for (char c = 'A'; c <= 'Z'; c++) {
            table.add(String.valueOf(c));
        }

        while (table.size() < 48) {
            table.add(null);
        }

        for (int i = 0; i < 10; i++) {
            table.add(String.valueOf(i));
        }

        table.set(32, " ");

        return table.toArray(new String[0]);
    }
}
