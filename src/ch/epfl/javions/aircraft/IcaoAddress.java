package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

public record IcaoAddress(String string){
    public IcaoAddress {
        Preconditions.checkArgument(string.length() == 6);
    }
}
