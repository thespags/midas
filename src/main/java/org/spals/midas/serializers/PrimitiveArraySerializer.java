package org.spals.midas.serializers;

import java.lang.reflect.Array;

class PrimitiveArraySerializer implements Serializer<Object> {

    @Override
    public byte[] serialize(Object value) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < Array.getLength(value); i++) {
            if (builder.length() > 1) {
                builder.append(", ");
            }
            builder.append(String.valueOf(Array.get(value, i)));
        }
        builder.append("]");
        return Converter.toUtf8(builder.toString());
    }
}