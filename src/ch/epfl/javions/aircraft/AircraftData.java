package ch.epfl.javions.aircraft;

import java.util.Objects;

public record AircraftData(AircraftDescription aircraftDescription, IcaoAddress icaoAddress,String model, AircraftRegistration aircraftRegistration, WakeTurbulenceCategory wakeTurbulenceCategory) {
    public AircraftData{
        Objects.requireNonNull(aircraftDescription);
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(model);
        Objects.requireNonNull(aircraftRegistration);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}