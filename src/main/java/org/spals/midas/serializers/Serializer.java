package org.spals.midas.serializers;

public interface Serializer<T> {
    byte[] serialize(T input);
}
