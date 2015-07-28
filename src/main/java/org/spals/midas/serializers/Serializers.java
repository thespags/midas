package org.spals.midas.serializers;

/**
 * TODO clean this up.
 */
public class Serializers {

    private static final Serializer<Object> OBJECT = new ToStringSerializer<>();

    private Serializers() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> of() {
        return (Serializer<T>) OBJECT;
    }
}
