package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;
/**
 * Represents the call sign (indicatif) of an aircraft.
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 * @param string
 */
public record CallSign(String string) {
    private static final Pattern patternCallSign = Pattern.compile("[A-Z0-9 ]{0,8}");

    public CallSign {
        Preconditions.checkArgument(patternCallSign.matcher(string).matches() || string.isEmpty());
    }
}
