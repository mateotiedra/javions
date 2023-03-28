package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Represents a message received from an aircraft.
 *
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 */
public interface Message {
    /**
     * Returns the timestamp of the message, in nanoseconds.
     *
     * @return the timestamp of the message, in nanoseconds
     */
    long timeStampNs();

    /**
     * Returns the ICAO address of the sender of the message.
     *
     * @return the ICAO address of the sender of the message
     */
    IcaoAddress icaoAddress();
}
