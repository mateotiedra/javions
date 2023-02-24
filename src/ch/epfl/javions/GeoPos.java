package ch.epfl.javions;

/**
 * A class that represents a geographical position.
 *
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 **/
public record GeoPos(int longitudeT32, int latitudeT32) {
    /**
     * Constructs a new GeoPos object.
     *
     * @param longitudeT32 The longitude in T32 units.
     * @param latitudeT32  The latitude in T32 units.
     * @throws IllegalArgumentException if the latitude is not valid.
     **/
    public GeoPos {
        Preconditions.checkArgument(isValidLatitudeT32(longitudeT32));
    }

    /**
     * Checks if the given integer value is a valid T32 latitude value.
     *
     * @param latitudeT32 The integer value to be checked.
     * @return true if the given value is a valid T32 latitude value.
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        return (latitudeT32 >= -Math.scalb(-1, 30) && latitudeT32 <= Math.scalb(-1, 30));
    }

    /**
     * Returns the longitude in radians.
     *
     * @return The longitude in radians.
     */
    public double longitude() {
        return Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.RADIAN);
    }

    /**
     * Returns the latitude in radians.
     *
     * @return The latitude in radians.
     */
    public double latitude() {
        return Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.RADIAN);
    }

    /**
     * Returns a string representation of the GeoPos object.
     *
     * @return A string representation of the GeoPos object.
     */
    @Override
    public String toString() {
        double longitudeDegree = Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE);
        double latitudeDegree = Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE);
        return ("(" + longitudeDegree + "°, " + latitudeDegree + "°)");
    }
}
