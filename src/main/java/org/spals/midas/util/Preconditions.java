package org.spals.midas.util;

/**
 * Guava like functionality without a guava dependency.
 */
public class Preconditions {
    public static <T> T checkNotNull(final T value, final String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }

    public static void checkArgument(final boolean value, final String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }
}
