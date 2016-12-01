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

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * Handles primitive arrays which can be handled by the generic {@link ArraySerializer}.
 *
 * @author spags
 */
class PrimitiveArraySerializer implements Serializer<Object> {

    private final SerializerMap serializers;

    PrimitiveArraySerializer(final SerializerMap serializers) {
        Objects.requireNonNull(serializers, "bad serializer map");
        this.serializers = serializers;
    }

    /**
     * @param value the primitive array to be serialized
     * @return the bytes of the serialized primitive array
     */
    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize(final Object value) {
        final StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < Array.getLength(value); i++) {
            if (builder.length() > 1) {
                builder.append(", ");
            }
            final Object o = Array.get(value, i);
            builder.append(Strings.get().decode(serializers.getUnsafe(o.getClass()).serialize(o)));
        }
        builder.append("]");
        return Strings.get().encode(builder.toString());
    }
}