package ch.epfl.javions;

/**
 * A utility class to check the validity of arguments.
 *
 * @author Kevan Lam (356395)
 * @author Mateo Tiedra (356525)
 **/
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Checks that the argument is correct.
     *
     * @param shouldBeTrue The argument to be checked.
     * @throws IllegalArgumentException if the argument is false.
     **/
    static void checkArgument(boolean shouldBeTrue) throws IllegalArgumentException {
        if (!shouldBeTrue) throw new IllegalArgumentException();

    }
}
