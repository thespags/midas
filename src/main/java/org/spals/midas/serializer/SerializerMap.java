/*
 * Copyright (c) 2015, James T Spagnola & Timothy P Kral
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.spals.midas.serializer;

import org.spals.midas.util.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A mapping of types to their serializer.
 * Provides default behavior for serializes that were specified.
 * <br>1. Arrays, return the array serializer
 * <br>2. If no exact class was found, it will use assignability to determine the next best serializer
 *
 * @author spags
 */
class SerializerMap {

    private final Map<Class<?>, Serializer<?>> serializers;
    private final PrimitiveArraySerializer arraySerializer;

    private SerializerMap() {
        this.serializers = new LinkedHashMap<>();
        this.arraySerializer = new PrimitiveArraySerializer(this);
    }

    public static SerializerMap make() {
        return new SerializerMap();
    }

    <T> SerializerMap put(final Class<T> clazz, final Serializer<T> serializer) {
        Objects.requireNonNull(clazz, "null class provided");
        Objects.requireNonNull(serializer, "null serializer provided");
        Preconditions.checkArgument(!serializers.containsKey(clazz), "duplicate class: " + clazz);
        serializers.put(clazz, serializer);
        return this;
    }

    <T> SerializerMap putIfMissing(final Class<T> clazz, final Serializer<T> serializer) {
        Objects.requireNonNull(clazz, "null class provided");
        Objects.requireNonNull(serializer, "null serializer provided");
        if (!serializers.containsKey(clazz)) {
            serializers.put(clazz, serializer);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    <T> Serializer<T> get(final Class<T> clazz) {
        return (Serializer<T>) getUnsafe(clazz);
    }

    Serializer getUnsafe(final Class<?> clazz) {
        if (serializers.containsKey(clazz)) {
            return serializers.get(clazz);
        }
        if (clazz.isArray()) {
            return arraySerializer;
        }
        // No exact class, try to find an assignable one.
        for (final Map.Entry<Class<?>, Serializer<?>> entry : serializers.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Adds primitive serializer, plus String, Map, and Iterable.
     */
    SerializerMap putJava() {
        putIfMissing(boolean.class, Serializers.of());
        putIfMissing(Boolean.class, Serializers.of());

        putIfMissing(short.class, Serializers.of());
        putIfMissing(Short.class, Serializers.of());

        putIfMissing(int.class, Serializers.of());
        putIfMissing(Integer.class, Serializers.of());

        putIfMissing(long.class, Serializers.of());
        putIfMissing(Long.class, Serializers.of());

        putIfMissing(double.class, Serializers.of());
        putIfMissing(Double.class, Serializers.of());

        putIfMissing(float.class, Serializers.of());
        putIfMissing(Float.class, Serializers.of());

        putIfMissing(char.class, Serializers.of());
        putIfMissing(Character.class, Serializers.of());

        putIfMissing(byte.class, Serializers.of());
        putIfMissing(Byte.class, Serializers.of());

        putIfMissing(String.class, Serializers.of());

        putIfMissing(Map.class, new MapSerializer(this));
        putIfMissing(Iterable.class, new IterableSerializer(this));
        return this;
    }
}
