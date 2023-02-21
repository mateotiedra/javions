package ch.epfl.javions;

public final class Preconditions {
    private Preconditions(){};
    void checkArgument(boolean shouldBeTrue) throws IllegalArgumentException{
            if(!shouldBeTrue) throw new IllegalArgumentException();
    }
}
