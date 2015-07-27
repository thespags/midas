package org.spals.midas.serializers;

import java.util.Arrays;

class ArraySerializer<T> implements Serializer<T[]> {

    private final Serializer<T> element;

    public ArraySerializer(final Serializer<T> element) {
        this.element = element;
    }

    @Override
    public byte[] serialize(T[] array) {
        return Serializers.ofIterable(element).serialize(Arrays.asList(array));
    }
}
