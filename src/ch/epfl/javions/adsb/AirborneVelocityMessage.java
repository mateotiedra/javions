package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading) {

    private static final int ST_POS = 48;
    private static final int ST_SIZE = 3;

    private static final int V_SIZE = 10;
    private static final int DIR_SIZE = 1;

    private static final int SH_POSE = 21;
    private static final int SH_SIZE = 1;
    private static final int HDG_POS = 11;
    private static final int HDG_SIZE = 10;
    private static final int AS_SIZE = 10;

    private static final int ENCODED_AIR_SPEED_RATIO = 1 << 10;


    public AirborneVelocityMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(speed >= 0);
        Preconditions.checkArgument(trackOrHeading >= 0);
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        int st = Bits.extractUInt(payload, ST_POS, ST_SIZE);
        Velocity velocity;

        try {
            if (st == 1 || st == 2) {
                velocity = computeGroundSpeed(payload, st == 2);
            } else if (st == 3 || st == 4) {
                velocity = computeAirSpeed(payload, st == 4);
            } else {
                return null;
            }

            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), velocity.speed, velocity.angle);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private record Velocity(double speed, double angle) {
        public Velocity {
            Preconditions.checkArgument(speed >= 0);
            Preconditions.checkArgument(angle >= 0);
        }

    }

    private static Velocity computeGroundSpeed(long payload, boolean factorFour) {
        int dew = Bits.extractUInt(payload, V_SIZE + DIR_SIZE + V_SIZE, DIR_SIZE);
        int vew = Bits.extractUInt(payload, V_SIZE + DIR_SIZE, V_SIZE) - 1;
        int dns = Bits.extractUInt(payload, V_SIZE, DIR_SIZE);
        int vns = Bits.extractUInt(payload, 0, V_SIZE) - 1;

        double angle = Math.atan2(vew * dew, vns * dns);
        double speed = Math.hypot(vns - 1, vew - 1) * (factorFour ? 4 : 1);

        return new Velocity(Units.convertFrom(speed, Units.Speed.KNOT), angle);
    }

    private static Velocity computeAirSpeed(long payload, boolean factorFour) {
        int as = Bits.extractUInt(payload, 0, AS_SIZE);
        int hdg = Bits.extractUInt(payload, HDG_POS, HDG_SIZE);
        int sh = Bits.extractUInt(payload, SH_POSE, SH_SIZE);

        int angle = sh == 1 ? hdg / ENCODED_AIR_SPEED_RATIO : 0;
        int speed = (as - 1) * (factorFour ? 4 : 1);

        return new Velocity(Units.convertFrom(speed, Units.Speed.KNOT), angle);
    }
}
