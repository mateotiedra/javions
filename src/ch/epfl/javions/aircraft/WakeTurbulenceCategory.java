package ch.epfl.javions.aircraft;

/**
 * This enum represents the wake turbulence category of an aircraft. (ps UNKNOWN for like a baloon)
 *
 * @author Kevan Lam (356395)
 */
public enum WakeTurbulenceCategory {
    LIGHT, MEDIUM, HEAVY, UNKNOWN;

    /**
     * Returns the WakeTurbulenceCategory corresponding to the given string.
     *
     * @param s the string to convert
     * @return the WakeTurbulenceCategory corresponding to the given string
     */
    public static WakeTurbulenceCategory of(String s) {
        return switch (s) {
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };
    }
}
