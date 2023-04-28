package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

public final class MapParameters {
    private final IntegerProperty zoom = new SimpleIntegerProperty();
    private final DoubleProperty minX = new SimpleDoubleProperty(), minY = new SimpleDoubleProperty();

    /**
     * Create a new map parameters object with the given zoom level and top left corner coordinates.
     *
     * @param zoom the zoom level, between 6 and 19
     * @param minX the x value of the top left corner of the map
     * @param minY the y value of the top left corner of the map
     */
    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument(zoom >= 6 && zoom <= 19);
        this.zoom.set(zoom);
        this.minX.set(minX);
        this.minY.set(minY);
    }

    /**
     * Get the zoom level property.
     *
     * @return the zoom level property
     */
    public ReadOnlyIntegerProperty zoomProperty() {
        return zoom;
    }

    /**
     * Get the zoom level.
     *
     * @return the zoom level
     */
    public int getZoom() {
        return zoom.get();
    }

    /**
     * Get the x value of the top left corner of the map property.
     *
     * @return the x value of the top left corner of the map property
     */
    public ReadOnlyDoubleProperty minXProperty() {
        return minX;
    }

    /**
     * Get the x value of the top left corner of the map.
     *
     * @return the x value of the top left corner of the map
     */
    public double getMinX() {
        return minX.get();
    }

    /**
     * Get the y value of the top left corner of the map property.
     *
     * @return the y value of the top left corner of the map property
     */
    public ReadOnlyDoubleProperty minYProperty() {
        return minY;
    }


    /**
     * Get the y value of the top left corner of the map.
     *
     * @return the y value of the top left corner of the map
     */
    public double getMinY() {
        return minY.get();
    }

    /**
     * Scroll the map by the given delta x and y.
     *
     * @param deltaX the delta x
     * @param deltaY the delta y
     */
    public void scroll(double deltaX, double deltaY) {
        minX.add(deltaX);
        minY.add(deltaY);
    }

    /**
     * Change the zoom level by the given delta zoom level.
     *
     * @param deltaZoom the delta zoom level, positive for zooming in, negative for zooming out. Will set it min at 6 and max at 19.
     */
    public void changeZoomLevel(int deltaZoom) {
        int oldZoom = getZoom();
        zoom.set(Math2.clamp(6, oldZoom + deltaZoom, 19));
        int clampedDeltaZoom = getZoom() - oldZoom;

        minX.set(Math.scalb(minX.get(), clampedDeltaZoom));
        minY.set(Math.scalb(minY.get(), clampedDeltaZoom));
    }
}
