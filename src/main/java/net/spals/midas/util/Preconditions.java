package net.spals.midas.util;

/**
 * Guava like functionality without a guava dependency.
 *
 * @author spags
 */
public final class Preconditions {

    private Preconditions() {
    }

    public static void checkArgument(final boolean value, final String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }
}
