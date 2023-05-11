package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.util.Objects;

public final class ObservableAircraftState implements AircraftStateSetter {
    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;

    private final LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private long lastAirbornePositionMessageTimeStampNs;
    private final IntegerProperty category = new SimpleIntegerProperty();
    private final ObjectProperty<CallSign> callsign = new SimpleObjectProperty<>();
    private final ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private final ObservableList<AirbornePos> trajectory = FXCollections.observableArrayList();
    private final ObservableList<AirbornePos> unmodifiableTrajectory = FXCollections.unmodifiableObservableList(trajectory);
    private final DoubleProperty altitude = new SimpleDoubleProperty();
    private final DoubleProperty velocity = new SimpleDoubleProperty();
    private final DoubleProperty trackOrHeading = new SimpleDoubleProperty();

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        this.icaoAddress = Objects.requireNonNull(icaoAddress);
        this.aircraftData = Objects.requireNonNull(aircraftData);
    }

    public record AirbornePos(GeoPos position, double altitude) {
        public AirbornePos {
            Preconditions.checkArgument(altitude >= 0);
        }

        public double getWebMercatorPosX(int zoomLevel) {
            return WebMercator.x(zoomLevel, position.longitude());
        }

        public double getWebMercatorPosY(int zoomLevel) {
            return WebMercator.y(zoomLevel, position.latitude());
        }
    }

    // IcaoAddress
    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }

    // AircraftData
    public AircraftData getAircraftData() {
        return aircraftData;
    }

    // Last message time stamp

    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }

    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs.set(timeStampNs);
    }

    // Category

    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    public int getCategory() {
        return category.get();
    }

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    // Callsign

    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callsign;
    }

    public CallSign getCallSign() {
        return callsign.get();
    }

    @Override
    public void setCallSign(CallSign callsign) {
        this.callsign.set(callsign);
    }

    // Trajectory

    public ObservableList<AirbornePos> getTrajectory() {
        return unmodifiableTrajectory;
    }

    private void updateTrajectory() {
        AirbornePos point = new AirbornePos(getPosition(), getAltitude());

        if (trajectory.isEmpty() || !trajectory.get(trajectory.size() - 1).equals(point)) {
            trajectory.add(point);
            lastAirbornePositionMessageTimeStampNs = getLastMessageTimeStampNs();
        } else if (lastAirbornePositionMessageTimeStampNs == getLastMessageTimeStampNs()) {
            trajectory.set(trajectory.size() - 1, point);
        }
    }

    // Position

    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    public GeoPos getPosition() {
        return position.get();
    }

    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
        updateTrajectory();
    }

    // Altitude
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    public double getAltitude() {
        return altitude.get();
    }

    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        updateTrajectory();
    }

    // Velocity

    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    public double getVelocity() {
        return velocity.get();
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    // Track or heading

    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        Preconditions.checkArgument(0 <= trackOrHeading && trackOrHeading <= Math.PI * 2);
        this.trackOrHeading.set(trackOrHeading);
    }
}
