package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the description of an aircraft.
 * @author Kevan Lam (356395)
 * @param string
 */
public record AircraftDescription(String string) {
    private static final Pattern patternDescription = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
    public AircraftDescription {
        Preconditions.checkArgument(patternDescription.matcher(string).matches() || string.isEmpty());
    }
}
