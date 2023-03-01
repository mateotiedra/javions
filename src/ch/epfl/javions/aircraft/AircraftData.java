package ch.epfl.javions.aircraft;

public record AircraftData(AircraftDescription aircraftDescription, IcaoAddress icaoAddress, AircraftRegistration aircraftRegistration, WakeTurbulenceCategory wakeTurbulenceCategory) {

}
