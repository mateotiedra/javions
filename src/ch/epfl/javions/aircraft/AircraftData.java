package ch.epfl.javions.aircraft;

import java.util.Objects;

public record AircraftData(AircraftRegistration aircraftRegistration, AircraftTypeDesignator typeDesignator,String model, AircraftDescription aircraftDescription, WakeTurbulenceCategory wakeTurbulenceCategory) {
    public AircraftData{
        Objects.requireNonNull(aircraftDescription);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(aircraftRegistration);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}