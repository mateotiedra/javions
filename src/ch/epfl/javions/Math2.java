package ch.epfl.javions;

/**
 * A utility class for mathematical functions.
 *
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 **/
public final class Math2 {
    private Math2() {
    }

    /**
     * Clamps a value between a minimum and a maximum.
     *
     * @param min The minimum value.
     * @param v   The value to be clamped.
     * @param max The maximum value.
     * @return The clamped value.
     * @throws IllegalArgumentException if the minimum is greater than the maximum.
     **/
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min < max);
        if (v < min) return min;
        else if (v > max) return max;
        else return v;
    }

    /**
     * Calculates the inverse hyperbolic sine of a given value.
     *
     * @param x The value to calculate the inverse hyperbolic sine of.
     * @return The inverse hyperbolic sine of the given value.
     */
    public static double asinh(double x) {
        return Math.log((x + Math.sqrt(1 + Math.pow(x, 2))));
    }
}
