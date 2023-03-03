package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * This class represents the data of an aircraft.
 *
 * @author Kevan Lam (356395)
 */
public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator,
                           String model, AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {
    public AircraftData {
        Objects.requireNonNull(description);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(registration);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}