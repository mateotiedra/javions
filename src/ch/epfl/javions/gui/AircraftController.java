package ch.epfl.javions.gui;

import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftData;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.ListIterator;

public final class AircraftController {

    private static final double ESTIMATED_HIGHEST_ALTITUDE = 12000;
    private final MapParameters mp;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;

    private final Pane pane = new Pane();

    public AircraftController(MapParameters mp, ObservableSet<ObservableAircraftState> visibleAircraft, ObjectProperty<ObservableAircraftState> selectedAircraft) {
        this.mp = mp;
        this.selectedAircraft = selectedAircraft;

        pane.getStylesheets().add("aircraft.css");

        for (ObservableAircraftState aircraft : visibleAircraft) {
            pane.getChildren().add(buildAircraftGroup(aircraft));
        }

        visibleAircraft.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            ObservableAircraftState aircraft = change.getElementAdded();
            if (change.wasAdded()) {
                pane.getChildren().add(buildAircraftGroup(aircraft));
            } else if (change.wasRemoved()) {
                pane.getChildren().removeIf(group -> group.getId().equals(aircraft.getIcaoAddress().string()));
            }
        });
    }

    public Pane pane() {
        return pane;
    }

    private Group buildAircraftGroup(ObservableAircraftState aircraft) {
        Group aircraftGroup = new Group();
        aircraftGroup.setId(aircraft.getIcaoAddress().string());
        aircraftGroup.viewOrderProperty().bind(aircraft.altitudeProperty().negate());

        aircraftGroup.getChildren().add(buildTrajectoryGroup(aircraft));
        aircraftGroup.getChildren().add(buildLabelGroup(aircraft));
        aircraftGroup.getChildren().add(buildIconGroup(aircraft));

        return aircraftGroup;
    }

    private Group buildTrajectoryGroup(ObservableAircraftState aircraft) {
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");

        trajectoryGroup.layoutXProperty().bind(mp.minXProperty());
        trajectoryGroup.layoutYProperty().bind(mp.minYProperty());

        trajectoryGroup.visibleProperty().bind(selectedAircraft.isEqualTo(aircraft));

        aircraft.getTrajectory().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>) change ->
                addLinesToGroup(trajectoryGroup, mp, aircraft.getTrajectory())
        );
        mp.zoomProperty().addListener(zoom ->
                addLinesToGroup(trajectoryGroup, mp, aircraft.getTrajectory())
        );
        selectedAircraft.addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(aircraft)) addLinesToGroup(trajectoryGroup, mp, aircraft.getTrajectory());
        });

        return trajectoryGroup;
    }

    private void addLinesToGroup(Group trajectoryGroup, MapParameters mp, ObservableList<ObservableAircraftState.AirbornePos> trajectory) {
        if (!trajectoryGroup.isVisible()) {
            return;
        }

        ListIterator<ObservableAircraftState.AirbornePos> iterator = trajectory.listIterator();
        if (!iterator.hasNext()) {
            return;
        }

        trajectoryGroup.getChildren().clear();

        ObservableAircraftState.AirbornePos pos = iterator.next();

        while (iterator.hasNext()) {
            ObservableAircraftState.AirbornePos nextPos = iterator.next();

            Line line = new Line(
                    pos.getWebMercatorPosX(mp.getZoom()) - mp.getMinX(),
                    pos.getWebMercatorPosY(mp.getZoom()) - mp.getMinY(),
                    nextPos.getWebMercatorPosX(mp.getZoom()) - mp.getMinX(),
                    nextPos.getWebMercatorPosY(mp.getZoom()) - mp.getMinY()
            );

            trajectoryGroup.getChildren().add(line);

            pos = nextPos;
        }
    }

    private Group buildLabelGroup(ObservableAircraftState aircraft) {
        Group labelGroup = new Group();
        labelGroup.getStyleClass().add("label");

        Rectangle rect = new Rectangle();
        Text text = new Text();

        // Bind the label text to the aircraft data and the speed and altitude properties
        String registration = aircraft.getAircraftData().registration().string();

        text.textProperty().bind(Bindings.format("%d\n%vkm/h\u2002%am",
                (!registration.isEmpty() ? registration
                        : (!aircraft.getCallSign().string().isEmpty()) ? aircraft.getCallSign().string()
                        : aircraft.getIcaoAddress().string()),
                aircraft.velocityProperty(),
                aircraft.altitudeProperty()
        ));

        text.textProperty().bind(Bindings.createStringBinding(() -> {
                    String firstLine = (!registration.isEmpty() ? registration
                            : (!aircraft.getCallSign().string().isEmpty()) ? aircraft.getCallSign().string()
                            : aircraft.getIcaoAddress().string());
                    return String.format("%s\n%fkm/h\u2002%fm", firstLine, aircraft.getVelocity(), aircraft.getAltitude());
                },
                aircraft.callSignProperty(),
                aircraft.velocityProperty(),
                aircraft.altitudeProperty()
        ));

        // Bind the rect width to the text width
        rect.widthProperty().bind(text.layoutBoundsProperty().map(b -> b.getWidth() + 4));

        // Bind the label position to the aircraft position
        labelGroup.layoutXProperty().bind(aircraft.positionProperty().map(pos -> WebMercator.x(mp.getZoom(), pos.longitude()) - mp.getMinX()));
        labelGroup.layoutYProperty().bind(aircraft.positionProperty().map(pos -> WebMercator.y(mp.getZoom(), pos.latitude()) - mp.getMinY()));

        // Bind the label visibility to the zoom level and the selectedAircraft property
        labelGroup.visibleProperty().bind(mp.zoomProperty().greaterThanOrEqualTo(11).or(selectedAircraft.isEqualTo(aircraft)));

        labelGroup.getChildren().add(rect);
        labelGroup.getChildren().add(text);

        return labelGroup;
    }

    private Group buildIconGroup(ObservableAircraftState aircraft) {
        Group iconGroup = new Group();
        iconGroup.getStyleClass().add("aircraft");

        AircraftData data = aircraft.getAircraftData();

        ObjectProperty<AircraftIcon> iconProperty = new SimpleObjectProperty<>();
        iconProperty.bind(aircraft.categoryProperty().map(
                category -> AircraftIcon.iconFor(data.typeDesignator(), data.description(), aircraft.getCategory(), data.wakeTurbulenceCategory())
        ));

        SVGPath svgPath = new SVGPath();
        svgPath.contentProperty().bind(iconProperty.map(AircraftIcon::svgPath));
        svgPath.rotateProperty().bind(aircraft.trackOrHeadingProperty().map(track -> iconProperty.get().canRotate() ? track : 0));
        svgPath.fillProperty().bind(aircraft.altitudeProperty().map(alt -> ColorRamp.PLASMA.at(Math.pow(alt.doubleValue() / ESTIMATED_HIGHEST_ALTITUDE, 1d / 3d))));

        iconGroup.getChildren().add(svgPath);

        return iconGroup;
    }
}
