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

import java.util.Map;
import java.util.stream.Collectors;

/**
 * For a given {@code Map<K, V>}, this will use the {@link Serializer} provided for K, V to serialize.
 * <pre>
 * This will be represented as (serialize(K) => serialize(V), ...)
 * </pre>
 *
 * @author spags
 */
class MapSerializer implements Serializer {

    private final SerializerRegistry registry;

    MapSerializer(final SerializerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String fileExtension() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param input the map to be serialized
     * @return the bytes of the serialized map
     */
    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize(final Object input) {
        final Map<?, ?> map = (Map<?, ?>) input;
        return StringEncoding.get().encode(
            map.entrySet().stream()
                .map(entry -> decode(entry.getKey()) + " -> " + decode(entry.getValue()))
                .collect(Collectors.joining(", ", "(", ")"))
        );
    }

    private <T> String decode(final T x) {
        final byte[] bytes = registry.getUnsafe(x.getClass())
            .orElseThrow(() -> new RuntimeException("No serializer found for type " + x.getClass()))
            .serialize(x);

        return StringEncoding.get().decode(bytes);
    }
}
