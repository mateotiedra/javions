package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.Objects;

public final class BaseMapController {
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
    private static void setZoomManager(Pane pane, MapParameters mp) {
        final LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            mp.scroll(e.getX(), e.getY());
            mp.changeZoomLevel(1);
            mp.scroll(-e.getX(), -e.getY());
        });
    }

    /**
     * Set the listeners that will manage the movement of the map.
     *
     * @param pane the pane to listen to
     * @param mp   the map parameters
     */
    private static void setMapMovementManager(Pane pane, MapParameters mp) {
        ObjectProperty<Point2D> lastMousePos = new SimpleObjectProperty<>();
        pane.setOnMousePressed(e -> lastMousePos.set(new Point2D(e.getX(), e.getY())));

        pane.setOnMouseDragged(e -> {
            if (lastMousePos.get() == null) return;

            double deltaX = e.getX() - lastMousePos.get().getX();
            double deltaY = e.getY() - lastMousePos.get().getY();

            mp.scroll(deltaX, deltaY);

            lastMousePos.get().add(deltaX, deltaY);
        });

        pane.setOnMouseReleased(e -> lastMousePos.set(null));
    }

    public Pane pane() {
        return pane;
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        double xPos = mp.getMinX();
        double yPos = mp.getMinY();

        double xShift = Math.floor(xPos / 256) * 256 - mp.getMinX();
        double yShift = Math.floor(yPos / 256) * 256 - mp.getMinY();

        while (xPos < mp.getMinX() + pane.getWidth()) {
            while (yPos < mp.getMinY() + pane.getHeight()) {
                Image tileImg = tileManager.imageForTileAt(new TileManager.TileId(mp.getZoom(), (int) (xPos / 256), (int) (yPos / 256)));
                gc.drawImage(tileImg, xPos - mp.getMinX() + xShift, yPos - mp.getMinY() + yShift);

                yPos += TileManager.TILE_SIZE;
            }
            xPos += TileManager.TILE_SIZE;
        }
    }

    /**
     * Center the map on the given position.
     *
     * @param newCenter the new center position
     */
    public void centerOne(GeoPos newCenter) {
        double newX = WebMercator.x(mp.getZoom(), newCenter.longitude());
        double newY = WebMercator.y(mp.getZoom(), newCenter.latitude());

        mp.scroll(newX - pane.getWidth() / 2 - mp.getMinX(), newY - pane.getHeight() / 2 - mp.getMinY());
        redrawOnNextPulse();
    }
}
