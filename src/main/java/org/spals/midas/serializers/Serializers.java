package org.spals.midas.serializers;

/**
 * Provides a factory for standard serializes.
 */
public final class Serializers {

    public static final Serializer<Object> OBJECT = new ToStringSerializer<>();

    private Serializers() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> of() {
        return (Serializer<T>) OBJECT;
    }
}