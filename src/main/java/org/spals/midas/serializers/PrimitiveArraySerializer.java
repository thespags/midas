package org.spals.midas.serializers;

import com.google.common.base.Preconditions;

import java.lang.reflect.Array;

/**
 * Converts a primitive array to [1, 2, 3, ...]
 */
class PrimitiveArraySerializer implements Serializer<Object> {

    private final SerializerMap serializers;

    public PrimitiveArraySerializer(final SerializerMap serializers) {
        Preconditions.checkNotNull(serializers);
        this.serializers = serializers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize(Object value) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < Array.getLength(value); i++) {
            if (builder.length() > 1) {
                builder.append(", ");
            }
            Object o = Array.get(value, i);
            builder.append(Converter.fromUtf8(serializers.getUnsafe(o.getClass()).serialize(o)));
        }
        builder.append("]");
        return Converter.toUtf8(builder.toString());
    }
}