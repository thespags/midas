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

import com.google.common.annotations.VisibleForTesting;

import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link Serializer} which attempts to create a {@link String}
 * representation of an input {@link Object}.
 *
 * @author spags
 * @author tkral
 */
class ToStringSerializer implements Serializer {

    private static final SerializerRegistry REGISTRY = SerializerRegistry.newDefault();

    ToStringSerializer() {
    }

    /**
     * @see Serializer#serialize(Object)
     */
    @Override
    public byte[] serialize(final Object input) {
        return Optional.ofNullable(input)
            .map(this::toStringSerialize)
            .orElseGet(() -> StringEncoding.get().encode(StringEncoding.NULL));
    }

    @VisibleForTesting
    byte[] toStringSerialize(final Object input) {
        checkNotNull(input);
        final String inputString = input.toString();

        // Check to see if we'd get the same String if the given input
        // was a pure Object. If so, that means there's no toString()
        // implementation in the input's class hierarchy. So we'll
        // attempt to circumnavigate this with reflection.
        if (Objects.equals(inputString, toObjectString(input))) {
            final Optional<Serializer> registeredSerializer = REGISTRY.get(input.getClass());
            // See if we have a pre-registered serializer available for this type
            return registeredSerializer.map(serializer -> serializer.serialize(input))
                // Fallback to the reflection serializer
                .orElseGet(() -> new ReflectionSerializer().serialize(input));
        }

        // Otherwise, just return whatever the implemented toString()
        // gave us.
        return StringEncoding.get().encode(inputString);
    }

    // Construct the String of the given input in pure Object form
    @VisibleForTesting
    String toObjectString(final Object input) {
        return input.getClass().getName() + "@" + Integer.toHexString(input.hashCode());
    }
}