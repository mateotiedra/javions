package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

/**
 * A controller for a base map.
 *
 * @author Mateo Tiedra (356525)
 */

public final class BaseMapController {
    private static final int MIN_TIME_BETWEEN_SCROLLS_IN_MS = 100;
    private final TileManager tileManager;
    private final MapParameters mp;
    private final Pane pane = new Pane();
    private final Canvas canvas = new Canvas();

    private boolean redrawNeeded = true;

    /**
     * Create a new base map controller with the given tile manager and map parameters.
     *
     * @param tileManager   the tile manager
     * @param mapParameters the map parameters
     */
    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = Objects.requireNonNull(tileManager);
        this.mp = Objects.requireNonNull(mapParameters);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        pane.getChildren().add(canvas);

        setZoomManager(pane, mp);
        setMapMovementManager(pane, mp);

        // Redraw listeners
        mp.zoomProperty().addListener((p, o, n) -> redrawOnNextPulse());
        mp.minXProperty().addListener((p, o, n) -> redrawOnNextPulse());
        mp.minYProperty().addListener((p, o, n) -> redrawOnNextPulse());
        pane.widthProperty().addListener((p, o, n) -> redrawOnNextPulse());
        pane.heightProperty().addListener((p, o, n) -> redrawOnNextPulse());
    }

    /**
     * Set the listeners that will manage the zoom of the map.
     *
     * @param pane the pane to listen to
     * @param mp   the map parameters
     */
    private void setZoomManager(Pane pane, MapParameters mp) {
        final LongProperty minTimeForNextScroll = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minTimeForNextScroll.get()) return;
            minTimeForNextScroll.set(currentTime + MIN_TIME_BETWEEN_SCROLLS_IN_MS);

            mp.scroll(e.getX(), e.getY());
            mp.changeZoomLevel(zoomDelta);
            mp.scroll(-e.getX(), -e.getY());
        });
    }

    /**
     * Set the listeners that will manage the movement of the map.
     *
     * @param pane the pane to listen to
     * @param mp   the map parameters
     */
    private void setMapMovementManager(Pane pane, MapParameters mp) {
        ObjectProperty<Point2D> lastMousePos = new SimpleObjectProperty<>();
        pane.setOnMousePressed(e -> lastMousePos.set(new Point2D(e.getX(), e.getY())));

        pane.setOnMouseDragged(e -> {
            double deltaX = lastMousePos.get().getX() - e.getX();
            double deltaY = lastMousePos.get().getY() - e.getY();

            mp.scroll(deltaX, deltaY);
            lastMousePos.set(new Point2D(e.getX(), e.getY()));
        });
    }

    /**
     * Get the pane containing the map.
     *
     * @return the pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Call the refresh of the map on the next pulse.
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Redraw the map if needed.
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        double xPos = mp.getMinX();
        double yPos = mp.getMinY();

        double xShift = Math.floor(xPos / TileManager.TILE_SIZE) * TileManager.TILE_SIZE - mp.getMinX();
        double yShift = Math.floor(yPos / TileManager.TILE_SIZE) * TileManager.TILE_SIZE - mp.getMinY();

        while (xPos + xShift < mp.getMinX() + pane.getWidth()) {
            while (yPos + yShift < mp.getMinY() + pane.getHeight()) {
                try {
                    int xTile = (int) (xPos / TileManager.TILE_SIZE);
                    int yTile = (int) (yPos / TileManager.TILE_SIZE);

                    if (TileManager.TileId.isValid(mp.getZoom(), xTile, yTile)) {
                        Image tileImg = tileManager.imageForTileAt(new TileManager.TileId(mp.getZoom(), xTile, yTile));
                        gc.drawImage(tileImg, xPos - mp.getMinX() + xShift, yPos - mp.getMinY() + yShift);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                yPos += TileManager.TILE_SIZE;
            }
            yPos = mp.getMinY();
            xPos += TileManager.TILE_SIZE;
        }
    }


    /**
     * Center the map on the given position.
     *
     * @param newCenter the new center position
     */
    public void centerOn(GeoPos newCenter) {
        double newX = WebMercator.x(mp.getZoom(), newCenter.longitude());
        double newY = WebMercator.y(mp.getZoom(), newCenter.latitude());

        mp.scroll(newX - pane.getWidth() / 2 - mp.getMinX(), newY - pane.getHeight() / 2 - mp.getMinY());
        redrawOnNextPulse();
    }
}
