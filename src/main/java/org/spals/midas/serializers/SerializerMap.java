package org.spals.midas.serializers;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

public class SerializerMap {

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

    public Serializer<?> getUnsafe(Class<?> clazz) {
        Preconditions.checkNotNull(clazz, "null class provided");
        return Preconditions.checkNotNull(serializers.get(clazz), "missing serializer: " + clazz);
    }
}
