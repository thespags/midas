package org.spals.midas.serializers;

/**
 * TODO clean this up.
 */
public class Serializers {

    public static final Serializer<Object> OBJECT = new ToStringSerializer<>();
    public static final Serializer<Object[]> OBJECT_ARRAY = new ArraySerializer<>(Serializers.of());
    static final Serializer<Object> PRIMITIVE_ARRAY = new PrimitiveArraySerializer();

    private Serializers() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> ofArray() {
        return (Serializer<T>) OBJECT_ARRAY;
    }

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> of() {
        return (Serializer<T>) OBJECT;
    }

    @SuppressWarnings("unchecked")
    static <T> Serializer<T> ofPrimitiveArray() {
        return (Serializer<T>) PRIMITIVE_ARRAY;
    }

    public static <T> Serializer<Iterable<T>> ofIterable(final Serializer<T> element) {
        return new IterableSerializer<>(element);
    }
}
