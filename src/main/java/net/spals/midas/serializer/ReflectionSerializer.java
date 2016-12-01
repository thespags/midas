/*
 * Copyright (c) 2016, James T Spagnola & Timothy P Kral
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.spals.midas.serializer;

import net.spals.midas.GoldFileException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A serializer that uses reflection to print all non null field values for an entity. The serializer is customizable
 * and should only be used for a top level call to serialize, ie don't register a serializer of this type for a class.
 * <pre>
 * You can register default java serializer {@link Builder#registerJava()}
 * You can register a specific class type serializer {@link Builder#register(Class, Serializer)}
 * You can register a default serializer if no specific one is found {@link Builder#registerDefault(Serializer)}
 * You can specify which field(s) to serialize {@link Builder#registerField(String)} or {@link Builder#registerFields(String...)}
 * You can specify null fields to be written as '&lt;null&gt;' {@link Builder#writeNull}
 * </pre>
 *
 * @author spags
 */
public final class ReflectionSerializer<T> implements Serializer<T> {

    private final SerializerMap serializers;
    private final boolean writeNull;
    private final Set<String> filteredFields;
    private final boolean filterFields;
    private final Serializer<Object> defaultSerializer;

    private ReflectionSerializer(final Builder builder) {
        serializers = builder.serializers;
        writeNull = builder.writeNull;
        defaultSerializer = builder.defaultSerializer;
        filteredFields = builder.filteredFields;
        filterFields = !builder.filteredFields.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * @param input the object to be serialized
     * @return the bytes of the serialized input
     */
    @Override
    public byte[] serialize(final T input) {
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
                        builder.append(Strings.get().decode(getSerializer(clazz).serialize(fieldValue)));
                    } else {
                        // its null so emit this placeholder
                        builder.append(Strings.NULL);
                    }
                    builder.append("\n");
                }
            } catch (final IllegalAccessException e) {
                // This shouldn't happen because we set accessible to true.
                throw new GoldFileException("Couldn't access field " + field.getName());
            }
        }
        if (filterFields && !filteredFields.isEmpty()) {
            throw new IllegalStateException("unmatched fields: " + filteredFields);
        }
        return Strings.get().encode(builder.toString());
    }

    private Serializer getSerializer(final Class<?> clazz) {
        final Serializer<?> serializer = serializers.get(clazz);
        if (serializer == null) {
            return Objects.requireNonNull(defaultSerializer, "missing serializer: " + clazz);
        }
        return serializer;
    }

    public static final class Builder {

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
         *
         * @param field a field to be serialized
         * @return the current builder
         */
        public Builder registerField(final String field) {
            filteredFields.add(field);
            return this;
        }

        /**
         * Registers multiple fields see {@link #registerField(String)}.
         *
         * @param fields a list of fields to be serialized
         * @return the current builder
         */
        public Builder registerFields(final String... fields) {
            filteredFields.addAll(Arrays.asList(fields));
            return this;
        }

        /**
         * Null fields will be outputted
         *
         * @return the current builder
         */
        public Builder writeNull() {
            writeNull = true;
            return this;
        }

        /**
         * Registers a serializer for a specific type.
         *
         * @param <T>        type safes the method from {@link Class} to its {@link Serializer}
         * @param clazz      type to register a serializer for
         * @param serializer serialize that is registered
         * @return the current builder
         */
        public <T> Builder register(final Class<T> clazz, final Serializer<T> serializer) {
            serializers.put(clazz, serializer);
            return this;
        }

        /**
         * Registers a serializer that is used if no type specific one is defined.
         *
         * @param serializer serialize that is registered
         * @return the current builder
         */
        public Builder registerDefault(final Serializer<Object> serializer) {
            defaultSerializer = serializer;
            return this;
        }

        /**
         * This will use our standard java type serializers if not already specified for a type.
         * If a type has already been specified then its respective java type serializer will not be used.
         *
         * @return the current builder
         */
        public Builder registerJava() {
            serializers.putJava();
            return this;
        }

        /**
         * @param <T> the type of the serializer
         * @return an instance of {@link ReflectionSerializer} from the builder
         */
        public <T> ReflectionSerializer<T> build() {
            return new ReflectionSerializer<>(this);
        }
    }
}
