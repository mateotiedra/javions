package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;
/**
 * Represents the registration number (immatriculation) of an aircraft.
 * @author Kevan Lam (356395)
 * @param string
 */
public record AircraftRegistration(String string) {
    private static final Pattern patternImmatriculation = Pattern.compile("[A-Z0-9 .?/_+-]+");
    public AircraftRegistration {
        Preconditions.checkArgument(patternImmatriculation.matcher(string).matches());
    }
}
