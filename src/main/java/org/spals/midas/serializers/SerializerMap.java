package org.spals.midas.serializers;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

public class SerializerMap {

    // TODO move to equivalence wrapper?
    private final Map<Class<?>, Serializer<?>> serializers;

    private SerializerMap() {
        this.serializers = new HashMap<>();
    }

    public static SerializerMap make() {
        return new SerializerMap();
    }

    public <T> SerializerMap put(Class<T> clazz, Serializer<T> serializer) {
        Preconditions.checkNotNull(clazz, "null class provided");
        Preconditions.checkNotNull(serializer, "null serializer provided");
        Preconditions.checkArgument(!serializers.containsKey(clazz), "duplicate class: " + clazz);
        serializers.put(clazz, serializer);
        return this;
    }

    <T> SerializerMap putIfMissing(Class<T> clazz, Serializer<T> serializer) {
        Preconditions.checkNotNull(clazz, "null class provided");
        Preconditions.checkNotNull(serializer, "null serializer provided");
        if (!serializers.containsKey(clazz)) {
            serializers.put(clazz, serializer);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Serializer<T> get(Class<T> clazz) {
        return (Serializer<T>) getUnsafe(clazz);
    }

    Serializer getUnsafe(Class<?> clazz) {
        return Preconditions.checkNotNull(serializers.get(clazz), "missing serializer: " + clazz);
    }

    public SerializerMap putJava() {
        putIfMissing(boolean.class, Serializers.of());
        putIfMissing(boolean[].class, Serializers.ofPrimitiveArray());
        putIfMissing(Boolean.class, Serializers.of());
        putIfMissing(Boolean[].class, Serializers.ofPrimitiveArray());

        putIfMissing(short.class, Serializers.of());
        putIfMissing(short[].class, Serializers.ofPrimitiveArray());
        putIfMissing(Short.class, Serializers.of());
        putIfMissing(Short[].class, Serializers.ofPrimitiveArray());

        putIfMissing(int.class, Serializers.of());
        putIfMissing(int[].class, Serializers.ofPrimitiveArray());
        putIfMissing(Integer.class, Serializers.of());
        putIfMissing(Integer[].class, Serializers.ofPrimitiveArray());

        putIfMissing(long.class, Serializers.of());
        putIfMissing(long[].class, Serializers.ofPrimitiveArray());
        putIfMissing(Long.class, Serializers.of());
        putIfMissing(Long[].class, Serializers.ofPrimitiveArray());

        putIfMissing(double.class, Serializers.of());
        putIfMissing(double[].class, Serializers.ofPrimitiveArray());
        putIfMissing(Double.class, Serializers.of());
        putIfMissing(Double[].class, Serializers.ofPrimitiveArray());

        putIfMissing(float.class, Serializers.of());
        putIfMissing(float[].class, Serializers.ofPrimitiveArray());
        putIfMissing(Float.class, Serializers.of());
        putIfMissing(Float[].class, Serializers.ofPrimitiveArray());

        putIfMissing(char.class, Serializers.of());
        putIfMissing(char[].class, Serializers.ofPrimitiveArray());
        putIfMissing(Character.class, Serializers.of());
        putIfMissing(Character[].class, Serializers.ofPrimitiveArray());

        putIfMissing(byte.class, Serializers.of());
        putIfMissing(byte[].class, Serializers.ofPrimitiveArray());
        putIfMissing(Byte.class, Serializers.of());
        putIfMissing(Byte[].class, Serializers.ofPrimitiveArray());

        putIfMissing(String.class, Serializers.of());
        putIfMissing(String[].class, Serializers.ofPrimitiveArray());

        putIfMissing(Map.class, new MapSerializer(this));
        return this;
    }
}
