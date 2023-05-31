package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import static javafx.scene.paint.CycleMethod.NO_CYCLE;

/**
 * The aircraft controller is responsible for displaying aircraft on the map and updating their position, altitude, etc.
 *
 * @author Mateo Tiedra (356525)
 */
public final class AircraftController {
    private static final double ESTIMATED_HIGHEST_ALTITUDE = 12000;
    private static final double ZOOM_RATIO = 1d / 3d;
    private final MapParameters mp;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;

    private final Pane pane = new Pane();

    /**
     * Create a new aircraft controller with the given map parameters, visible aircraft and selected aircraft.
     *
     * @param mp               the map parameters
     * @param visibleAircraft  the visible aircraft
     * @param selectedAircraft the selected aircraft
     */
    public AircraftController(MapParameters mp, ObservableSet<ObservableAircraftState> visibleAircraft,
                              ObjectProperty<ObservableAircraftState> selectedAircraft) {
        this.mp = mp;
        this.selectedAircraft = selectedAircraft;

        pane.getStylesheets().add("aircraft.css");
        pane.setPickOnBounds(false);

        for (ObservableAircraftState aircraft : visibleAircraft) {
            pane.getChildren().add(buildAircraftGroup(aircraft));
        }

        visibleAircraft.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                pane.getChildren().add(buildAircraftGroup(change.getElementAdded()));
            } else if (change.wasRemoved()) {
                pane.getChildren().removeIf(group -> group.getId().equals(change.getElementRemoved().getIcaoAddress().string()));
            }
        });
    }

    /**
     * Get the pane containing the aircraft
     *
     * @return the pane containing the aircraft
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Get the aircraft group corresponding to the given aircraft.
     *
     * @param aircraft the aircraft
     * @return the aircraft group corresponding to the given aircraft
     */
    private Group buildAircraftGroup(ObservableAircraftState aircraft) {
        Group aircraftGroup = new Group();
        aircraftGroup.setId(aircraft.getIcaoAddress().string());
        aircraftGroup.viewOrderProperty().bind(aircraft.altitudeProperty().negate());

        Group movingGroup = new Group();

        // Bind the moving group position to the aircraft position
        movingGroup.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> WebMercator.x(mp.getZoom(), aircraft.getPosition().longitude()) - mp.getMinX(),
                mp.zoomProperty(),
                mp.minXProperty(),
                aircraft.positionProperty()));
        movingGroup.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> WebMercator.y(mp.getZoom(), aircraft.getPosition().latitude()) - mp.getMinY(),
                mp.zoomProperty(),
                mp.minYProperty(),
                aircraft.positionProperty()));

        movingGroup.getChildren().addAll(buildLabelGroup(aircraft), buildIconSvgPath(aircraft));
        aircraftGroup.getChildren().addAll(buildTrajectoryGroup(aircraft), movingGroup);

        return aircraftGroup;
    }

    /**
     * Build the label group for the given aircraft.
     *
     * @param aircraft the aircraft
     * @return the label group for the given aircraft
     */
    private Group buildTrajectoryGroup(ObservableAircraftState aircraft) {
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");

        trajectoryGroup.layoutXProperty().bind(mp.minXProperty().negate());
        trajectoryGroup.layoutYProperty().bind(mp.minYProperty().negate());

        trajectoryGroup.visibleProperty().bind(selectedAircraft.isEqualTo(aircraft));

        aircraft.getTrajectory().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>) change ->
                trajectoryGroup.getChildren().setAll(trajectoryGroup.isVisible() ? getTrajectoryLines(mp, aircraft.getTrajectory()) : Set.of())
        );
        mp.zoomProperty().addListener(zoom ->
                trajectoryGroup.getChildren().setAll(trajectoryGroup.isVisible() ? getTrajectoryLines(mp, aircraft.getTrajectory()) : Set.of())
        );
        selectedAircraft.addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(aircraft))
                trajectoryGroup.getChildren().setAll(trajectoryGroup.isVisible() ? getTrajectoryLines(mp, aircraft.getTrajectory()) : Set.of());
        });

        return trajectoryGroup;
    }

    /**
     * Get the trajectory lines for the given trajectory.
     *
     * @param mp         the map parameters
     * @param trajectory the trajectory
     * @return the trajectory lines for the given trajectory
     */
    private Set<Line> getTrajectoryLines(MapParameters mp, ObservableList<ObservableAircraftState.AirbornePos> trajectory) {
        ListIterator<ObservableAircraftState.AirbornePos> iterator = trajectory.listIterator();
        Set<Line> trajectoryLines = new HashSet<>();
        if (!iterator.hasNext()) {
            return trajectoryLines;
        }

        ObservableAircraftState.AirbornePos pos = iterator.next();

        while (iterator.hasNext()) {
            if (pos.position() == null) {
                pos = iterator.next();
                continue;
            }

            ObservableAircraftState.AirbornePos nextPos = iterator.next();
            if (nextPos.position() == null) continue;

            Line line = new Line(
                    pos.getWebMercatorPosX(mp.getZoom()),
                    pos.getWebMercatorPosY(mp.getZoom()),
                    nextPos.getWebMercatorPosX(mp.getZoom()),
                    nextPos.getWebMercatorPosY(mp.getZoom())
            );

            if (pos.altitude() == nextPos.altitude()) {
                line.setStroke(ColorRamp.PLASMA.at(getFractionFromAltitude(pos.altitude())));
            } else {
                Stop s1 = new Stop(0, ColorRamp.PLASMA.at(getFractionFromAltitude(pos.altitude())));
                Stop s2 = new Stop(1, ColorRamp.PLASMA.at(getFractionFromAltitude(nextPos.altitude())));
                line.setStroke(new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, s1, s2));
            }

            trajectoryLines.add(line);

            pos = nextPos;
        }

        return trajectoryLines;
    }

    /**
     * Build the label group for the given aircraft.
     *
     * @param aircraft the aircraft
     * @return the label group for the given aircraft
     */
    private Group buildLabelGroup(ObservableAircraftState aircraft) {
        Group labelGroup = new Group();
        labelGroup.getStyleClass().add("label");

        Rectangle rect = new Rectangle();
        Text text = new Text();

        // Bind the label text to the aircraft data and the speed and altitude properties
        String registration = (aircraft.getAircraftData() != null) ? aircraft.getAircraftData().registration().string() : "";

        text.textProperty().bind(Bindings.createStringBinding(() -> {
                    String identification = (!registration.isEmpty() ? registration
                            : (aircraft.getCallSign() != null) ? aircraft.getCallSign().string()
                            : aircraft.getIcaoAddress().string());

                    String velocity = Double.isNaN(aircraft.getVelocity()) ? "?" :
                            String.valueOf(Math.round(Units.convert(aircraft.getVelocity(), Units.Speed.METER_PER_SECOND, Units.Speed.KILOMETER_PER_HOUR)));

                    String altitude = Double.isNaN(aircraft.getAltitude()) ? "?" : String.valueOf(Math.round(aircraft.getAltitude()));

                    return String.format("%s\n%s km/h\u2002%s m", identification, velocity, altitude);
                },
                aircraft.callSignProperty(),
                aircraft.velocityProperty(),
                aircraft.altitudeProperty()
        ));

        // Bind the rect width to the text width
        rect.widthProperty().bind(text.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rect.heightProperty().bind(text.layoutBoundsProperty().map(b -> b.getHeight() + 4));

        // Bind the label visibility to the zoom level and the selectedAircraft property
        labelGroup.visibleProperty().bind(mp.zoomProperty().greaterThanOrEqualTo(11).or(selectedAircraft.isEqualTo(aircraft)));

        labelGroup.getChildren().addAll(rect, text);

        return labelGroup;
    }

    /**
     * Build the icon SVGPath for the given aircraft.
     *
     * @param aircraft the aircraft
     * @return the icon SVGPath for the given aircraft
     */
    private SVGPath buildIconSvgPath(ObservableAircraftState aircraft) {
        SVGPath svgPath = new SVGPath();
        svgPath.getStyleClass().add("aircraft");

        svgPath.setOnMouseClicked(e -> selectedAircraft.set(aircraft));

        AircraftData data = aircraft.getAircraftData();

        ObjectProperty<AircraftIcon> iconProperty = new SimpleObjectProperty<>();
        iconProperty.bind(aircraft.categoryProperty().map(
                category -> data == null
                        ? AircraftIcon.iconFor(new AircraftTypeDesignator(""), new AircraftDescription(""), aircraft.getCategory(), WakeTurbulenceCategory.UNKNOWN)
                        : AircraftIcon.iconFor(data.typeDesignator(), data.description(), aircraft.getCategory(), data.wakeTurbulenceCategory())
        ));

        svgPath.contentProperty().bind(iconProperty.map(AircraftIcon::svgPath));
        svgPath.rotateProperty().bind(Bindings.createDoubleBinding(
                () -> iconProperty.get().canRotate() ? Units.convert(aircraft.getTrackOrHeading(), Units.Angle.RADIAN, Units.Angle.DEGREE) : 0,
                iconProperty,
                aircraft.trackOrHeadingProperty()));
        svgPath.fillProperty().bind(aircraft.altitudeProperty().map(alt -> ColorRamp.PLASMA.at(getFractionFromAltitude(alt.doubleValue()))));

        return svgPath;
    }

    /**
     * Get the result of the formula given to choose the color corresponding to the altitude.
     *
     * @param alt the altitude
     * @return the fraction corresponding to the altitude
     */
    private double getFractionFromAltitude(double alt) {
        return Math.pow(alt / ESTIMATED_HIGHEST_ALTITUDE, ZOOM_RATIO);
    }
}
