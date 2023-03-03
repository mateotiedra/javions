package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;
/**
 * Represents the ICAO address (international civil aviation organization) of an aircraft.(unique number for each aircraft)
 * @author Kevan Lam (356395)
 * @param string
 */
public record IcaoAddress(String string){
    private static final Pattern patternICAO = Pattern.compile("[0-9A-F]{6}");
    public IcaoAddress {
        Preconditions.checkArgument(patternICAO.matcher(string).matches());
    }
}
