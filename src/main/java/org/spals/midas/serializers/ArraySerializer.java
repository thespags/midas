package org.spals.midas.serializers;

import com.google.common.base.Preconditions;

import java.util.Arrays;

/**
 * Defers logic to {@link IterableSerializer}, as a list.
 */
class ArraySerializer<T> implements Serializer<T[]> {

    private final IterableSerializer serializer;

    public ArraySerializer(final SerializerMap serializers) {
        Preconditions.checkNotNull(serializers);
        this.serializer = new IterableSerializer(serializers);
    }

    @Override
    public String serialize(final T[] array) {
        return serializer.serialize(Arrays.asList(array));
    }
}
