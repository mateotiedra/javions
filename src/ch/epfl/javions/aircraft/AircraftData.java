package ch.epfl.javions.aircraft;

import java.util.Objects;
/**
 * This class represents the data of an aircraft.
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 *
 */
public record AircraftData(AircraftRegistration aircraftRegistration, AircraftTypeDesignator typeDesignator,String model, AircraftDescription aircraftDescription, WakeTurbulenceCategory wakeTurbulenceCategory) {
    public AircraftData{
        Objects.requireNonNull(aircraftDescription);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(aircraftRegistration);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}