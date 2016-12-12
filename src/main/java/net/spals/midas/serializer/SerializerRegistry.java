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

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A mapping of types to their serializer.
 * Provides default behavior for serializes that were specified.
 * <pre>
 * - Arrays, return the array serializer
 * - If no exact class was found, it will use assignability to determine the next best serializer
 * </pre>
 *
 * @author spags
 */
public class SerializerRegistry {

    private final Map<Class<?>, Serializer> serializers;

    private SerializerRegistry() {
        serializers = new LinkedHashMap<>();
    }

    public static SerializerRegistry newDefault() {
        final SerializerRegistry registry = new SerializerRegistry();

        final ArraySerializer arraySerializer = new ArraySerializer(registry);
        final PrimitiveArraySerializer primitiveArraySerializer = new PrimitiveArraySerializer();
        final ToStringSerializer toStringSerializer = new ToStringSerializer(registry);

        registry.put(boolean.class, toStringSerializer);
        registry.put(boolean[].class, primitiveArraySerializer);
        registry.put(Boolean.class, toStringSerializer);
        registry.put(Boolean[].class, arraySerializer);

        registry.put(short.class, toStringSerializer);
        registry.put(short[].class, primitiveArraySerializer);
        registry.put(Short.class, toStringSerializer);
        registry.put(Short[].class, arraySerializer);

        registry.put(int.class, toStringSerializer);
        registry.put(int[].class, primitiveArraySerializer);
        registry.put(Integer.class, toStringSerializer);
        registry.put(Integer[].class, arraySerializer);

        registry.put(long.class, toStringSerializer);
        registry.put(long[].class, primitiveArraySerializer);
        registry.put(Long.class, toStringSerializer);
        registry.put(Long[].class, arraySerializer);

        registry.put(double.class, toStringSerializer);
        registry.put(double[].class, primitiveArraySerializer);
        registry.put(Double.class, toStringSerializer);
        registry.put(Double[].class, arraySerializer);

        registry.put(float.class, toStringSerializer);
        registry.put(float[].class, primitiveArraySerializer);
        registry.put(Float.class, toStringSerializer);
        registry.put(Float[].class, arraySerializer);

        registry.put(char.class, toStringSerializer);
        registry.put(char[].class, primitiveArraySerializer);
        registry.put(Character.class, toStringSerializer);
        registry.put(Character[].class, arraySerializer);

        registry.put(byte.class, toStringSerializer);
        registry.put(byte[].class, primitiveArraySerializer);
        registry.put(Byte.class, toStringSerializer);
        registry.put(Byte[].class, arraySerializer);

        registry.put(BigDecimal.class, toStringSerializer);
        registry.put(BigDecimal[].class, arraySerializer);
        registry.put(BigInteger.class, toStringSerializer);
        registry.put(BigInteger[].class, arraySerializer);

        registry.put(String.class, toStringSerializer);
        registry.put(String[].class, arraySerializer);

        registry.put(Map.class, new MapSerializer(registry));
        registry.put(Iterable.class, new IterableSerializer(registry));

        return registry;
    }

    public static SerializerRegistry newEmpty() {
        return new SerializerRegistry();
    }

    private <T> void put(final Class<T> clazz, final Serializer serializer) {
        Objects.requireNonNull(clazz, "null class provided");
        Objects.requireNonNull(serializer, "null serializer provided");
        Preconditions.checkArgument(!serializers.containsKey(clazz), "duplicate class: " + clazz);
        serializers.put(clazz, serializer);
    }

    public <T> void put(final Class<T> clazz, final TypedSerializer<T> serializer) {
        Objects.requireNonNull(clazz, "null class provided");
        Objects.requireNonNull(serializer, "null serializer provided");
        Preconditions.checkArgument(!serializers.containsKey(clazz), "duplicate class: " + clazz);
        serializers.put(clazz, serializer);
    }

    @SuppressWarnings("unchecked")
    <T> Optional<Serializer> get(final Class<T> clazz) {
        return getUnsafe(clazz);
    }

    Optional<Serializer> getUnsafe(final Class<?> clazz) {
        final Optional<Serializer> registeredSerializer = Optional.ofNullable(serializers.get(clazz));
        if (registeredSerializer.isPresent()) {
            return registeredSerializer;
        }

        // No exact class, try to find an assignable one.
        for (final Map.Entry<Class<?>, Serializer> entry : serializers.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return Optional.of(entry.getValue());
            }
        }

        return Optional.empty();
    }
}
