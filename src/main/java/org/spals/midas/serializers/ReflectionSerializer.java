package org.spals.midas.serializers;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class ReflectionSerializer<T> implements Serializer<T> {

    private final SerializerMap serializers;
    private final boolean writeNull;
    private Serializer<Object> defaultSerializer;

    private ReflectionSerializer(Builder builder) {
        this.serializers = builder.serializers;
        this.writeNull = builder.writeNull;
        this.defaultSerializer = builder.defaultSerializer;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public byte[] serialize(T input) {
        Field[] fields = input.getClass().getDeclaredFields();
        StringBuilder builder = new StringBuilder();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(input);

                // If the field is not null or we write null then emit
                if (fieldValue != null || writeNull) {
                    builder.append(field.getName())
                        .append(" = ");
                    if (fieldValue != null) {
                        Class clazz = field.getType();

                        //noinspection unchecked
                        builder.append(Converter.fromUtf8(getSerializer(clazz).serialize(fieldValue)).toCharArray());
                    } else {
                        // its null so emit this placeholder
                        builder.append("<null>");
                    }
                    builder.append("\n");
                }
            } catch (IllegalAccessException e) {
                /* This shouldn't happen because we have set accessible = true */
                throw new IllegalStateException("Couldn't access field " + field.getName());
            }
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private Serializer getSerializer(Class clazz) {
        Serializer<?> serializer = serializers.get(clazz);
        if (serializer == null) {
            return defaultSerializer;
        }
        return serializer;
    }

    public static class Builder {

        private SerializerMap serializers;
        private boolean writeNull;
        private Serializer<Object> defaultSerializer;

        private Builder() {
            serializers = SerializerMap.make();
        }

        public Builder writeNull() {
            writeNull = false;
            return this;
        }

        public <T> Builder register(Class<T> clazz, Serializer<T> serializer) {
            serializers.put(clazz, serializer);
            return this;
        }

        public Builder registerDefault(Serializer<Object> serializer) {
            defaultSerializer = serializer;
            return this;
        }

        public Builder registerPrimitives() {
            // TODO fill this in wiht more primitives
            serializers.put(int.class, Serializers.INTEGER);
            serializers.put(Integer.class, Serializers.INTEGER);
            serializers.put(byte.class, Serializers.BYTE);
            serializers.put(Byte.class, Serializers.BYTE);
            serializers.put(byte[].class, Serializers.BYTE_ARRAY);
            serializers.put(String.class, Serializers.STRING);
            serializers.put(String[].class, Serializers.STRING_ARRAY);
            serializers.put(Map.class, new MapSerializer(serializers));
            return this;
        }

        public <T> ReflectionSerializer<T> build() {
            return new ReflectionSerializer<>(this);
        }
    }
}
