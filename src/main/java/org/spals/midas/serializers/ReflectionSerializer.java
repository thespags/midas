package org.spals.midas.serializers;

import com.google.common.base.Preconditions;

import java.lang.reflect.Field;

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
                // This shouldn't happen because we set accessible to true.
                throw new IllegalStateException("Couldn't access field " + field.getName());
            }
        }
        return Converter.toUtf8(builder.toString());
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

        /**
         * Fields will be written even if null.
         */
        public Builder writeNull() {
            Preconditions.checkArgument(!writeNull, "already set write null to true");
            writeNull = true;
            return this;
        }

        public <T> Builder register(Class<T> clazz, Serializer<T> serializer) {
            serializers.put(clazz, serializer);
            return this;
        }

        /**
         * Registers a serializer that is used if no type specific is one defined.
         */
        public Builder registerDefault(Serializer<Object> serializer) {
            defaultSerializer = serializer;
            return this;
        }

        /**
         * Registers java serializers if they have not already been registered.
         */
        public Builder registerJava() {
            serializers.putJava();
            return this;
        }

        public <T> ReflectionSerializer<T> build() {
            return new ReflectionSerializer<>(this);
        }
    }
}
