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

/**
 * This class make the state of the aircraft observable.
 * It is used by the GUI to display aircraft.
 *
 * @author Mateo Tiedra (356525)
 */
public final class ObservableAircraftState implements AircraftStateSetter {
    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;
    private long lastAirbornePositionMessageTimeStampNs;

    private final LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private final IntegerProperty category = new SimpleIntegerProperty();
    private final ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    private final ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private final ObservableList<AirbornePos> trajectory = FXCollections.observableArrayList();
    private final ObservableList<AirbornePos> unmodifiableTrajectory = FXCollections.unmodifiableObservableList(trajectory);
    private final DoubleProperty altitude = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty velocity = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty trackOrHeading = new SimpleDoubleProperty(0);

    /**
     * Constructor of the class ObservableAircraftState
     *
     * @param icaoAddress  the IcaoAddress of the aircraft
     * @param aircraftData the AircraftData of the aircraft
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        this.icaoAddress = Objects.requireNonNull(icaoAddress);
        this.aircraftData = aircraftData;
    }

    /**
     * This class represents a position of an aircraft in the air
     * It is used to store the trajectory of an aircraft
     */
    public record AirbornePos(GeoPos position, double altitude) {
        /**
         * Constructor of the class AirbornePos
         *
         * @param position the position of the aircraft
         * @param altitude the altitude of the aircraft
         */
        public AirbornePos {
            Preconditions.checkArgument(altitude >= 0);
        }

        /**
         * This method returns the position of the aircraft in the Web Mercator projection
         *
         * @param zoomLevel the zoom level of the map
         * @return the x position of the aircraft in the WebMercator projection
         */
        public double getWebMercatorPosX(int zoomLevel) {
            return WebMercator.x(zoomLevel, position.longitude());
        }

        /**
         * This method returns the position of the aircraft in the WebMercator projection
         *
         * @param zoomLevel the zoom level of the map
         * @return the y position of the aircraft in the Web Mercator projection
         */
        public double getWebMercatorPosY(int zoomLevel) {
            return WebMercator.y(zoomLevel, position.latitude());
        }
    }

    /**
     * This method returns the IcaoAddress of the aircraft
     *
     * @return the IcaoAddress of the aircraft
     */
    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }

    /**
     * This method returns the AircraftData of the aircraft
     *
     * @return the AircraftData of the aircraft
     */
    public AircraftData getAircraftData() {
        return aircraftData;
    }

    // LastMessageTimeStampNs

    /**
     * This method returns the property of last message time stamp of the aircraft
     *
     * @return the property of the last message time stamp of the aircraft
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }

    /**
     * This method returns the last message time stamp of the aircraft
     *
     * @return the last message time stamp of the aircraft
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs.set(timeStampNs);
    }

    // Category

    /**
     * This method returns the property of the category of the aircraft
     *
     * @return the property of the category of the aircraft
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    /**
     * This method returns the property of the call sign of the aircraft
     *
     * @return the property of the call sign of the aircraft
     */
    public int getCategory() {
        return category.get();
    }

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    // CallSign

    /**
     * This method returns the property of the call sign of the aircraft
     *
     * @return the property of the call sign of the aircraft
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    /**
     * This method returns the call sign of the aircraft
     *
     * @return the call sign of the aircraft
     */
    public CallSign getCallSign() {
        return callSign.get();
    }

    @Override
    public void setCallSign(CallSign callsign) {
        this.callSign.set(callsign);
    }

    // Trajectory

    /**
     * This method an observable list of the trajectory of the aircraft
     *
     * @return the trajectory of the aircraft
     */
    public ObservableList<AirbornePos> getTrajectory() {
        return unmodifiableTrajectory;
    }

    // Position

    /**
     * This method returns the property of the position of the aircraft
     *
     * @return the property of the position of the aircraft
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    /**
     * This method returns the position of the aircraft
     *
     * @return the position of the aircraft, null if unknown
     */
    public GeoPos getPosition() {
        return position.get();
    }

    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);

        if (!Double.isNaN(getAltitude())) {
            trajectory.add(new AirbornePos(position, getAltitude()));
            lastAirbornePositionMessageTimeStampNs = getLastMessageTimeStampNs();
        }
    }

    // Altitude

    /**
     * This method returns the property of the altitude of the aircraft
     *
     * @return the property of the altitude of the aircraft
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    /**
     * This method returns the altitude of the aircraft
     *
     * @return the altitude of the aircraft, NaN if unknown
     */
    public double getAltitude() {
        return altitude.get();
    }

    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);

        if (getPosition() != null) {
            AirbornePos point = new AirbornePos(getPosition(), altitude);
            if (trajectory.isEmpty()) {
                trajectory.add(point);
            } else if (getLastMessageTimeStampNs() == lastAirbornePositionMessageTimeStampNs) {
                trajectory.set(trajectory.size() - 1, point);
            }
        }
    }

    // Velocity

    /**
     * This method returns the property of the velocity of the aircraft
     *
     * @return the property of the velocity of the aircraft
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    // Velocity

    /**
     * This method returns the velocity of the aircraft  in m/s
     *
     * @return the velocity of the aircraft, NaN if unknown
     */
    public double getVelocity() {
        return velocity.get();
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    // Track or heading

    /**
     * This method returns the property of the track or heading of the aircraft
     *
     * @return the property of the track or heading of the aircraft
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    /**
     * This method returns the track or heading of the aircraft in radians
     *
     * @return the track or heading of the aircraft, 0 by default
     */
    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        Preconditions.checkArgument(0 <= trackOrHeading && trackOrHeading <= Math.PI * 2);
        this.trackOrHeading.set(trackOrHeading);
    }
}
