package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the description of an aircraft.
 *
 * @author Kevan Lam (356395)
 */
public record AircraftDescription(String string) {
    private static final Pattern patternDescription = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * Constructs a new AircraftDescription.
     *
     * @param string the string to be used to construct the AircraftDescription
     * @throws IllegalArgumentException if the string does not match the pattern
     */
    public AircraftDescription {
        Preconditions.checkArgument(patternDescription.matcher(string).matches() || string.isEmpty());
    }
}
