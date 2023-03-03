package ch.epfl.javions.aircraft;
/**
 * This enum represents the wake turbulence category of an aircraft. (ps UNKNOWN for like a baloon)
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 *
 */
public enum WakeTurbulenceCategory {
    LIGHT, MEDIUM, HEAVY, UNKNOWN;
    public static WakeTurbulenceCategory of(String s) {
        switch (s) {
            case "L":
                return LIGHT;
            case "M":
                return MEDIUM;
            case "H":
                return HEAVY;
            default:
                return UNKNOWN;
        }
    }
}
