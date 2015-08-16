package org.spals.midas.serializer;

import org.spals.midas.util.Preconditions;

import java.lang.reflect.Array;

class PrimitiveArraySerializer implements Serializer<Object> {

    private final SerializerMap serializers;

    public PrimitiveArraySerializer(final SerializerMap serializers) {
        Preconditions.checkNotNull(serializers, "bad serializer map");
        this.serializers = serializers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String serialize(final Object value) {
        final StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < Array.getLength(value); i++) {
            if (builder.length() > 1) {
                builder.append(", ");
            }
            final Object o = Array.get(value, i);
            builder.append(serializers.getUnsafe(o.getClass()).serialize(o));
        }
        builder.append("]");
        return builder.toString();
    }
}