package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {
    private CprDecoder() {
    }

    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);
        final int Zphi0 = 60;
        final int Zphi1 = 59;
        final double majDeltaPhi0 = 1 / (double) Zphi0;
        final double majDeltaPhi1 = 1 / (double) Zphi1;

        double zphi = Math.rint(y0 * Zphi1 - y1 * Zphi0);
        double zphi0, zphi1;
        if (zphi < 0) {
            zphi0 = zphi + Zphi0;
            zphi1 = zphi + Zphi1;
        } else {
            zphi0 = zphi;
            zphi1 = zphi;
        }
        double phi0 = majDeltaPhi0 * (zphi0 + y0);
        double phi1 = majDeltaPhi1 * (zphi1 + y1);
        double A;
        if (1 - ((1 - Math.cos(2 * Math.PI * majDeltaPhi0)) / Math.pow((Math.cos(phi0)), 2)) > 1) A = 2 * Math.PI;
        else A = Math.acos(1 - ((1 - Math.cos(2 * Math.PI * majDeltaPhi0)) / Math.pow((Math.cos(phi0)), 2)));
        double Zlambda0 = Math.floor(2 * Math.PI / A);
        double Zlambda1 = Zlambda0 - 1;
        double zdelta = Math.rint(x0 * majDeltaPhi1 - x1 * majDeltaPhi0);
        double zdelta0, zdelta1, delta0 = 0, delta1 = 0;
        double majDeltaLambda0 = 1 / Zlambda0;
        double majDeltaLambda1 = 1 / Zlambda1;
        if (Zlambda0 == 1) {
            delta0 = x0;
            delta1 = x1;
        } else if (Zlambda0 > 1) {
            if (zdelta < 0) {
                zdelta0 = zdelta + Zlambda0;
                zdelta1 = zdelta + Zlambda1;
            } else {
                zdelta0 = zdelta;
                zdelta1 = zdelta;
            }
            delta0 = majDeltaLambda0 * (zdelta0 + x0);
            delta1 = majDeltaLambda1 * (zdelta1 + x1);
        }
        phi0 = Units.convert(phi0, Units.Angle.TURN, Units.Angle.T32);
        phi1 = Units.convert(phi1, Units.Angle.TURN, Units.Angle.T32);
        delta0 = Units.convert(delta0, Units.Angle.TURN, Units.Angle.T32);
        delta1 = Units.convert(delta1, Units.Angle.TURN, Units.Angle.T32);
        if ((Units.convert(phi0, Units.Angle.T32, Units.Angle.DEGREE) >= 90 && Units.convert(phi0, Units.Angle.T32, Units.Angle.DEGREE) <= -90)
                || (Units.convert(phi1, Units.Angle.T32, Units.Angle.DEGREE) >= 90 && Units.convert(phi1, Units.Angle.T32, Units.Angle.DEGREE) <= -90)
                || zphi0 != zphi1)
            return null;
        else if (mostRecent == 0) return new GeoPos((int) delta0, (int) phi0);
        else return new GeoPos((int) delta1, (int) phi1);
    }
}
