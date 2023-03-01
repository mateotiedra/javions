package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record CallSign(String string) {
    private static final Pattern patternCallSign = Pattern.compile("[A-Z0-9 ]{0,8}");

    public CallSign {
        Preconditions.checkArgument(patternCallSign.matcher(string).matches() || string.isEmpty());
    }
}
