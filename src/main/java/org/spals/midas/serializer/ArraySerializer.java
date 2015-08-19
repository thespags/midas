package org.spals.midas.serializer;

import java.util.Arrays;
import java.util.Objects;

/**
 * Defers logic to {@link IterableSerializer}, as a list.
 *
 * @author spags
 */
class ArraySerializer<T> implements Serializer<T[]> {

    private final IterableSerializer serializer;

    public ArraySerializer(final SerializerMap serializers) {
        Objects.requireNonNull(serializers, "bad serializer map");
        this.serializer = new IterableSerializer(serializers);
    }

    @Override
    public String serialize(final T[] array) {
        return serializer.serialize(Arrays.asList(array));
    }
}
