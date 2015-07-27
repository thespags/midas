package org.spals.midas.serializers;

import java.util.Map;

/**
 * TODO clean tihs up.
 */
public class Serializers {

    private static final Serializer<Object> PRIMITIVE_ARRAY = new PrimitiveArraySerializer();

    public static final Serializer<Object> OBJECT = new ToStringSerializer<>();

    public static final Serializer<Object[]> OBJECT_ARRAY = new ArraySerializer<>(Serializers.of());

    public static final Serializer<Byte> BYTE = of();

    public static final Serializer<String> STRING = of();

    public static final Serializer<Integer> INTEGER = of();

    public static final Serializer<Short> SHORT = of();

    public static final Serializer<Long> LONG = of();

    public static final Serializer<Float> FLOAT = of();

    public static final Serializer<Boolean> BOOLEAN = of();
    
    public static final Serializer<Double> DOUBLE = of();
    
    public static final Serializer<Character> CHAR = of();

    public static final Serializer<int[]> INTEGER_ARRAY = ofPrimitiveArray();
    
    public static final Serializer<byte[]> BYTE_ARRAY = ofPrimitiveArray();
    
    public static final Serializer<long[]> LONG_ARRAY = ofPrimitiveArray();
    
    public static final Serializer<short[]> SHORT_ARRAY = ofPrimitiveArray();
    
    public static final Serializer<double[]> DOUBLE_ARRAY = ofPrimitiveArray();
    
    public static final Serializer<boolean[]> BOOLEAN_ARRAY = ofPrimitiveArray();
    
    public static final Serializer<char[]> CHAR_ARRAY = ofPrimitiveArray();
    
    public static final Serializer<float[]> FLOAT_ARRAY = ofPrimitiveArray();

    public static final Serializer<String[]> STRING_ARRAY = ofArray();

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
    private static <T> Serializer<T> ofPrimitiveArray() {
        return (Serializer<T>) PRIMITIVE_ARRAY;
    }

    public static <T> Serializer<Iterable<T>> ofIterable(final Serializer<T> element) {
        return new IterableSerializer<>(element);
    }
}
