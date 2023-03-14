package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Represents a message received from an aircraft.
 * @author Kevan Lam (356395)
 */
public interface Message {
    //returns the timestamp of the message, in nanoseconds
    long timeStampNs();
    //returns the ICAO address of the sender of the message
    IcaoAddress icaoAddress();
}
