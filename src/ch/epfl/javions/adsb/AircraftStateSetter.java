package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * Represents the state of an aircraft.
 *
 * @author Kevan Lam (356395)
 */
public interface AircraftStateSetter {
    /**
     * changes the timestamp of the last message received from the aircraft to the given value
     *
     * @param timeStampNs
     */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * changes the category of the aircraft to the given value
     *
     * @param category
     */
    void setCategory(int category);

    /**
     * changes the callsign of the aircraft to the given value
     *
     * @param callsign
     */
    void setCallSign(CallSign callsign);

    /**
     * changes the position of the aircraft to the given value
     *
     * @param position
     */
    void setPosition(GeoPos position);

    /**
     * changes the altitude of the aircraft to the given value
     *
     * @param altitude
     */
    void setAltitude(double altitude);

    /**
     * changes the velocity of the aircraft to the given value
     *
     * @param velocity
     */
    void setVelocity(double velocity);

    /**
     * changes the direction of the aircraft to the given value
     *
     * @param trackOrHeading
     */
    void setTrackOrHeading(double trackOrHeading);
}
