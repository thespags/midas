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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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
class SerializerRegistry {

    private final Map<Class<?>, Serializer> serializers;
    private final PrimitiveArraySerializer arraySerializer;

    private SerializerRegistry() {
        serializers = new LinkedHashMap<>();
        arraySerializer = new PrimitiveArraySerializer(this);
    }

    static SerializerRegistry make() {
        return new SerializerRegistry();
    }

    <T> void put(final Class<T> clazz, final Serializer serializer) {
        Objects.requireNonNull(clazz, "null class provided");
        Objects.requireNonNull(serializer, "null serializer provided");
        Preconditions.checkArgument(!serializers.containsKey(clazz), "duplicate class: " + clazz);
        serializers.put(clazz, serializer);
    }

    private <T> void putIfMissing(final Class<T> clazz, final Serializer serializer) {
        Objects.requireNonNull(clazz, "null class provided");
        Objects.requireNonNull(serializer, "null serializer provided");
        serializers.computeIfAbsent(clazz, c -> serializer);
    }

    @SuppressWarnings("unchecked")
    <T> Serializer get(final Class<T> clazz) {
        return getUnsafe(clazz);
    }

    Serializer getUnsafe(final Class<?> clazz) {
        if (serializers.containsKey(clazz)) {
            return serializers.get(clazz);
        }
        if (clazz.isArray()) {
            return arraySerializer;
        }
        // No exact class, try to find an assignable one.
        for (final Map.Entry<Class<?>, Serializer> entry : serializers.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Adds primitive serializer, plus String, Map, and Iterable.
     */
    SerializerRegistry putJava() {
        putIfMissing(boolean.class, Serializers.newDefault());
        putIfMissing(Boolean.class, Serializers.newDefault());

        putIfMissing(short.class, Serializers.newDefault());
        putIfMissing(Short.class, Serializers.newDefault());

        putIfMissing(int.class, Serializers.newDefault());
        putIfMissing(Integer.class, Serializers.newDefault());

        putIfMissing(long.class, Serializers.newDefault());
        putIfMissing(Long.class, Serializers.newDefault());

        putIfMissing(double.class, Serializers.newDefault());
        putIfMissing(Double.class, Serializers.newDefault());

        putIfMissing(float.class, Serializers.newDefault());
        putIfMissing(Float.class, Serializers.newDefault());

        putIfMissing(char.class, Serializers.newDefault());
        putIfMissing(Character.class, Serializers.newDefault());

        putIfMissing(byte.class, Serializers.newDefault());
        putIfMissing(Byte.class, Serializers.newDefault());

        putIfMissing(String.class, Serializers.newDefault());

        putIfMissing(Map.class, new MapSerializer(this));
        putIfMissing(Iterable.class, new IterableSerializer(this));
        return this;
    }
}
