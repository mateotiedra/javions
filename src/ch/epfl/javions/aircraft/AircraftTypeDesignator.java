package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the type designator of an aircraft.
 *
 * @param string
 * @author Kevan Lam (356395)
 */
public record AircraftTypeDesignator(String string) {
    private static final Pattern patternType = Pattern.compile("[A-Z0-9]{2,4}");

    public AircraftTypeDesignator {
        Preconditions.checkArgument(patternType.matcher(string).matches() || string.isEmpty());
    }
}
