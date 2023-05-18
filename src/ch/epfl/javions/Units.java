package ch.epfl.javions;

/**
 * A utility class for unit conversions. And constants for units.
 *
 * @author Kevan Lam (356395)
 **/
public final class Units {
    private Units() {
    }

    /**
     * Centi tools for unit conversions.
     */
    public static final double CENTI = 1e-2;
    /**
     * Kilo tools for unit conversions.
     */
    public static final double KILO = 1e3;

    /**
     * Units for angles.
     */
    public static class Angle {
        private Angle() {
        }

        public static final double RADIAN = 1;
        public static final double TURN = 2 * Math.PI;
        public static final double DEGREE = TURN / 360;
        public static final double T32 = Math.scalb(TURN, -32);
    }

    /**
     * Units for lengths.
     */
    public static class Length {
        private Length() {
        }

        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54 * CENTIMETER;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE = 1852 * METER;
    }

    /**
     * Units for times.
     */
    public static class Time {
        private Time() {
        }

        public static final double SECOND = 1;
        public static final double MINUTE = 60 * SECOND;
        public static final double HOUR = 60 * MINUTE;
    }

    /**
     * Units for speeds.
     */
    public static class Speed {
        private Speed() {
        }

        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
        public static final double METER_PER_SECOND = Length.METER / Time.SECOND;
    }

    /**
     * Converts a value from one unit to another unit using the conversion ratio.
     *
     * @param value    The value to be converted.
     * @param fromUnit The conversion ratio from the current unit to the base unit.
     * @param toUnit   The conversion ratio of the target unit to the base unit.
     * @return The converted value in the target unit.
     * @throws IllegalArgumentException if either fromUnit or toUnit is zero.
     **/
    public static double convert(double value, double fromUnit, double toUnit) {
        return value * (fromUnit / toUnit);
    }


    /**
     * Converts a value from one unit to its base unit.
     *
     * @param value    The value to be converted.
     * @param fromUnit The conversion ratio from the current unit to the base unit.
     * @return The converted value in the base unit.
     * @throws IllegalArgumentException if fromUnit is zero.
     **/
    public static double convertFrom(double value, double fromUnit) {
        return convert(value, fromUnit, 1);
    }

    /**
     * Converts a value from its base unit to another unit.
     *
     * @param value  The value to be converted.
     * @param toUnit The conversion ratio from the target unit to the base unit.
     * @return The converted value in the target unit.
     * @throws IllegalArgumentException if toUnit is zero.
     **/
    public static double convertTo(double value, double toUnit) {
        return convert(value, 1, toUnit);
    }
}