package org.spals.midas.serializers;

import com.google.common.base.Preconditions;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A serializer that uses reflection to print all non null field values for an entity. The serializer is customizable
 * and should only be used for a top level call to serialize, ie don't register a serializer of this type for a class.
 * <br>You can register default java serializers {@link Builder#registerJava()}
 * <br>You can register a specific class type serializer {@link Builder#register(Class, Serializer)}
 * <br>You can register a default serializer if no specific one is found {@link Builder#registerDefault(Serializer)}
 * <br>You can specify which field(s) to serialize {@link Builder#registerField(String)} or {@link Builder#registerFields(String...)}
 * <br>You can specify null fields to be written as '&lt;null&gt;' {@link Builder#writeNull}
 */
public final class ReflectionSerializer<T> implements Serializer<T> {

    private final SerializerMap serializers;
    private final boolean writeNull;
    private final Set<String> filteredFields;
    private final boolean filterFields;
    private final Serializer<Object> defaultSerializer;

    private ReflectionSerializer(final Builder builder) {
        this.serializers = builder.serializers;
        this.writeNull = builder.writeNull;
        this.defaultSerializer = builder.defaultSerializer;
        this.filteredFields = builder.filteredFields;
        this.filterFields = !builder.filteredFields.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String serialize(final T input) {
        final Field[] fields = input.getClass().getDeclaredFields();
        final StringBuilder builder = new StringBuilder();
        for (final Field field : fields) {
            field.setAccessible(true);
            try {
                final Object fieldValue = field.get(input);
                // skip field if we are filtering
                if (filterFields && !filteredFields.remove(field.getName())) {
                    continue;
                }

                // If the field is not null or we write null then emit
                if (fieldValue != null || writeNull) {
                    builder.append(field.getName())
                        .append(" = ");
                    if (fieldValue != null) {
                        final Class clazz = field.getType();

                        //noinspection unchecked
                        builder.append(getSerializer(clazz).serialize(fieldValue));
                    } else {
                        // its null so emit this placeholder
                        builder.append("<null>");
                    }
                    builder.append("\n");
                }
            } catch (final IllegalAccessException e) {
                // This shouldn't happen because we set accessible to true.
                throw new IllegalStateException("Couldn't access field " + field.getName());
            }
        }
        if (filterFields && !filteredFields.isEmpty()) {
            throw new IllegalStateException("unmatched fields: " + filteredFields);
        }
        return builder.toString();
    }

    private Serializer getSerializer(final Class clazz) {
        final Serializer<?> serializer = serializers.get(clazz);
        if (serializer == null) {
            return Preconditions.checkNotNull(defaultSerializer, "missing serializer: " + clazz);
        }
        return serializer;
    }

    public static class Builder {

        private final Set<String> filteredFields;
        private final SerializerMap serializers;
        private boolean writeNull;
        private Serializer<Object> defaultSerializer;

        private Builder() {
            serializers = SerializerMap.make();
            filteredFields = new HashSet<>();
        }

        /**
         * By default all fields are printed, registering a field will limit the reflection to only those fields;
         */
        public Builder registerField(final String field) {
            filteredFields.add(field);
            return this;
        }

        /**
         * Registers multiple fields see {@link #registerField(String)}.
         */
        public Builder registerFields(final String... fields) {
            filteredFields.addAll(Arrays.asList(fields));
            return this;
        }

        public Builder writeNull() {
            writeNull = true;
            return this;
        }

        /**
         * Registers a serializer for a specific type.
         *
         * @param clazz      type to register a serializer for
         * @param serializer serialize that is registered
         */
        public <T> Builder register(final Class<T> clazz, final Serializer<T> serializer) {
            serializers.put(clazz, serializer);
            return this;
        }

        /**
         * Registers a serializer that is used if no type specific one is defined.
         */
        public Builder registerDefault(final Serializer<Object> serializer) {
            defaultSerializer = serializer;
            return this;
        }

        public Builder registerJava() {
            serializers.putJava();
            return this;
        }

        public <T> ReflectionSerializer<T> build() {
            return new ReflectionSerializer<>(this);
        }
    }
}
