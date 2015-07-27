package org.spals.midas.serializers;

class ToStringSerializer<T> implements Serializer<T> {
    @Override
    public byte[] serialize(T input) {
        return Converter.toUtf8(input.toString());
    }
}