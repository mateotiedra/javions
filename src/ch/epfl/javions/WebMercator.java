package ch.epfl.javions;

/**
 * This class is used to convert coordinates from the WGS84 coordinate system to the Web Mercator
 * coordinate system.
 *
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 **/
public class WebMercator {
    private WebMercator() {
    }

    /**
     * Converts a longitude from the WGS84 coordinate system to the Web Mercator coordinate system.
     *
     * @param zoomLevel The zoom level of the map.
     * @param longitude The longitude to be converted.
     * @return The converted longitude.
     **/
    public static double x(int zoomLevel, double longitude) {
        return Math.scalb((Units.convertTo(longitude, Units.Angle.TURN) + .5), 8 + zoomLevel);
    }

    /**
     * Converts a latitude from the WGS84 coordinate system to the Web Mercator coordinate system.
     *
     * @param zoomLevel The zoom level of the map.
     * @param latitude  The latitude to be converted.
     * @return The converted latitude.
     **/
    public static double y(int zoomLevel, double latitude) {
        return Math.scalb((Units.convertTo(-(Math2.asinh(Math.tan(latitude))), Units.Angle.TURN) + .5), 8 + zoomLevel);
    }
}
