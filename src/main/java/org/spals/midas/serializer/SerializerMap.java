package org.spals.midas.serializer;

import org.spals.midas.util.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A mapping of types to their serializer.
 * Provides default behavior for serializes that were specified.
 * <br>1. Arrays, return the array serializer
 * <br>2. If no exact class was found, it will use assignability to determine the next best serializer
 */
class SerializerMap {

    private final Map<Class<?>, Serializer<?>> serializers;
    private final PrimitiveArraySerializer arraySerializer;

    private SerializerMap() {
        this.serializers = new LinkedHashMap<>();
        this.arraySerializer = new PrimitiveArraySerializer(this);
    }

    public static SerializerMap make() {
        return new SerializerMap();
    }

    <T> SerializerMap put(final Class<T> clazz, final Serializer<T> serializer) {
        Preconditions.checkNotNull(clazz, "null class provided");
        Preconditions.checkNotNull(serializer, "null serializer provided");
        Preconditions.checkArgument(!serializers.containsKey(clazz), "duplicate class: " + clazz);
        serializers.put(clazz, serializer);
        return this;
    }

    <T> SerializerMap putIfMissing(final Class<T> clazz, final Serializer<T> serializer) {
        Preconditions.checkNotNull(clazz, "null class provided");
        Preconditions.checkNotNull(serializer, "null serializer provided");
        if (!serializers.containsKey(clazz)) {
            serializers.put(clazz, serializer);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    <T> Serializer<T> get(final Class<T> clazz) {
        return (Serializer<T>) getUnsafe(clazz);
    }

    Serializer getUnsafe(final Class<?> clazz) {
        if (serializers.containsKey(clazz)) {
            return serializers.get(clazz);
        }
        if (clazz.isArray()) {
            return arraySerializer;
        }
        // No exact class, try to find an assignable one.
        for (final Map.Entry<Class<?>, Serializer<?>> entry : serializers.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Adds primitive serializer, plus String, Map, and Iterable.
     */
    SerializerMap putJava() {
        putIfMissing(boolean.class, Serializers.of());
        putIfMissing(Boolean.class, Serializers.of());

        putIfMissing(short.class, Serializers.of());
        putIfMissing(Short.class, Serializers.of());

        putIfMissing(int.class, Serializers.of());
        putIfMissing(Integer.class, Serializers.of());

        putIfMissing(long.class, Serializers.of());
        putIfMissing(Long.class, Serializers.of());

        putIfMissing(double.class, Serializers.of());
        putIfMissing(Double.class, Serializers.of());

        putIfMissing(float.class, Serializers.of());
        putIfMissing(Float.class, Serializers.of());

        putIfMissing(char.class, Serializers.of());
        putIfMissing(Character.class, Serializers.of());

        putIfMissing(byte.class, Serializers.of());
        putIfMissing(Byte.class, Serializers.of());

        putIfMissing(String.class, Serializers.of());

        putIfMissing(Map.class, new MapSerializer(this));
        putIfMissing(Iterable.class, new IterableSerializer(this));
        return this;
    }
}
