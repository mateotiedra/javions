package ch.epfl.javions;

public final class Preconditions {
    private Preconditions(){}
    static void checkArgument(boolean shouldBeTrue) throws IllegalArgumentException{
            if(!shouldBeTrue) throw new IllegalArgumentException();
    }
}
