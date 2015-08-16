package org.spals.midas.serializer;

/**
 * Provides a factory for standard serializes.
 */
public final class Serializers {

    private static final Serializer<Object> OBJECT = new ToStringSerializer<>();

    private Serializers() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> of() {
        return (Serializer<T>) OBJECT;
    }
}
