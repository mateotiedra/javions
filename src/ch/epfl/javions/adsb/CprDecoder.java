package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * A class that decodes the CPR encoded position.
 *
 * @author Kevan Lam (356395)
 **/
public class CprDecoder {
    private static final int Zphi0 = 60;
    private static final int Zphi1 = 59;
    private static final double majDeltaPhi0 = 1 / (double) Zphi0;
    private static final double majDeltaPhi1 = 1 / (double) Zphi1;

    private CprDecoder() {
    }

    /**
     * Decodes the CPR encoded position.
     *
     * @param x0         The longitude of the first position.
     * @param y0         The latitude of the first position.
     * @param x1         The longitude of the second position.
     * @param y1         The latitude of the second position.
     * @param mostRecent The most recent position.
     * @return The decoded position.
     * @throws IllegalArgumentException if the most recent position is not 0 or 1.
     **/
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);

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
        phi0 = center(phi0);
        phi1 = center(phi1);
        double A_0, A_1;
        double argument_0 = 1 - ((1 - Math.cos(2 * Math.PI * majDeltaPhi0)) / (Math.pow((Math.cos(Units.convert(phi0, Units.Angle.TURN, Units.Angle.RADIAN))), 2)));
        double argument_1 = 1 - ((1 - Math.cos(2 * Math.PI * majDeltaPhi0)) / (Math.pow((Math.cos(Units.convert(phi1, Units.Angle.TURN, Units.Angle.RADIAN))), 2)));
        if (Double.isNaN(Math.acos(argument_0))) A_0 = 2 * Math.PI;
        else A_0 = Math.acos(argument_0);
        if (Double.isNaN(Math.acos(argument_1))) A_1 = 2 * Math.PI;
        else A_1 = Math.acos(argument_1);
        double Zlambda = Math.floor(2 * Math.PI / A_1);
        double Zlambda0 = Math.floor(2 * Math.PI / A_0);
        if (Zlambda != Zlambda0) return null;
        double Zlambda1 = Zlambda0 - 1;
        double zdelta = Math.rint(x0 * Zlambda1 - x1 * Zlambda0);
        double zdelta0, zdelta1, delta0 = 0, delta1 = 0;
        double majDeltaLambda0 = 1.0 / Zlambda0;
        double majDeltaLambda1 = 1.0 / Zlambda1;
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
        delta0 = center(delta0);
        delta1 = center(delta1);
        phi0 = Units.convert(phi0, Units.Angle.TURN, Units.Angle.T32);
        phi1 = Units.convert(phi1, Units.Angle.TURN, Units.Angle.T32);
        delta0 = Units.convert(delta0, Units.Angle.TURN, Units.Angle.T32);
        delta1 = Units.convert(delta1, Units.Angle.TURN, Units.Angle.T32);

        if (Units.convert(phi0, Units.Angle.T32, Units.Angle.DEGREE) >= 90 || Units.convert(phi0, Units.Angle.T32, Units.Angle.DEGREE) <= -90)
            return null;
        else if (Units.convert(phi1, Units.Angle.T32, Units.Angle.DEGREE) >= 90 || Units.convert(phi1, Units.Angle.T32, Units.Angle.DEGREE) <= -90)
            return null;
        else if (mostRecent == 0) return new GeoPos((int) Math.rint(delta0), (int) Math.rint(phi0));
        else return new GeoPos((int) Math.rint(delta1), (int) Math.rint(phi1));
    }

    /**
     * Centers the value.
     *
     * @param needToCenter argument to center
     * @return centered value
     */
    public static double center(double needToCenter) {
        return needToCenter >= 0.5 ? needToCenter - 1 : needToCenter;
    }
}
