package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.List;
import java.util.Objects;

/**
 * A message containing the position of an aircraft.
 *
 * @author Mateo Tiedra (356525)
 */

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x,
                                      double y) implements Message {

    private static final int ENCODED_ALTITUDE_POS = 36;
    private static final int ENCODED_ALTITUDE_SIZE = 12;
    private static final int Q_POS_IN_ALTITUDE_PART = 4;

    public AirbornePositionMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(parity == 0 || parity == 1);
        Preconditions.checkArgument(x >= 0 && x < 1);
        Preconditions.checkArgument(y >= 0 && y < 1);
    }

    /**
     * Create a new AirbornePositionMessage from a RawMessage.
     *
     * @param rawMessage : the RawMessage extract the position from
     * @return a new AirbornePositionMessage
     */
    public static AirbornePositionMessage of(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        long lon_cpr = Bits.extractUInt(payload, 0, 17);
        long lat_cpr = Bits.extractUInt(payload, 17, 17);
        int format = Bits.extractUInt(payload, 34, 1);
        double altitude;


        try {
            altitude = decodeAltitude(payload);
        } catch (Exception e) {
            return null;
        }

        return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), altitude, format, (double) lon_cpr / (1 << 17), (double) lat_cpr / (1 << 17));
    }

    /**
     * Decode the altitude from the payload.
     *
     * @param payload : the payload to decode the altitude from
     * @return the altitude in meters
     */
    private static double decodeAltitude(long payload) throws IllegalArgumentException {
        int encodedAltitude = Bits.extractUInt(payload, ENCODED_ALTITUDE_POS, ENCODED_ALTITUDE_SIZE);

        int q = Bits.extractUInt(encodedAltitude, Q_POS_IN_ALTITUDE_PART, 1);

        if (q == 0) {
            int msbAlt = decodeGrayCode(joinBitsByIndex(encodedAltitude, 4, 2, 0, 10, 8, 6, 5, 3, 1));      // D1 D2 D4 A1 A2 A4 B1 B2 B4
            int lsbAlt = decodeGrayCode(joinBitsByIndex(encodedAltitude, 11, 9, 7));                        // C1 C2 C4

            if (List.of(0, 5, 6).contains(lsbAlt)) {
                throw new IllegalArgumentException("Invalid altitude");
            } else if (lsbAlt == 7) {
                lsbAlt = 5;
            }

            if (msbAlt % 2 == 1) {
                lsbAlt = 6 - lsbAlt;
            }

            return Units.convertFrom(msbAlt * 500 + lsbAlt * 100 - 1300, Units.Length.FOOT);
        } else {
            long encodedAltitudeWithoutQ = Bits.extractUInt(encodedAltitude, 0, Q_POS_IN_ALTITUDE_PART)
                    | ((long) Bits.extractUInt(encodedAltitude, Q_POS_IN_ALTITUDE_PART + 1, ENCODED_ALTITUDE_SIZE - Q_POS_IN_ALTITUDE_PART - 1) << 4);

            return Units.convertFrom(25 * encodedAltitudeWithoutQ - 1000, Units.Length.FOOT);
        }
    }

    /**
     * Join the bits at the given indices.
     *
     * @param value   : the value to extract the bits from
     * @param indices : the indices of the bits to extract
     * @return the bits at the given indices
     */
    private static int joinBitsByIndex(int value, int... indices) {
        int result = 0;
        for (int i = 0; i < indices.length; i++) {
            result <<= i == 0 ? 0 : 1;
            result |= Bits.extractUInt(value, indices[i], 1);
        }
        return (result);
    }

    /**
     * Decode the gray code.
     *
     * @param grayCode : the gray code to decode
     * @return the decoded gray code
     */
    private static int decodeGrayCode(int grayCode) {
        int result = 0;
        while (grayCode > 0) {
            result ^= grayCode;
            grayCode >>= 1;
        }
        return result;
    }
}
