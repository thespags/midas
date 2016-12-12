/*
 * Copyright (c) 2016, spals
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
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A serializer that uses reflection to print all non null field values for an entity. The serializer is customizable
 * and should only be used for a top level call to serialize, ie don't register a serializer of this type for a class.
 *
 * @author spags
 * @author tkral
 */
final class ReflectionSerializer implements Serializer {

    private final SerializerRegistry registry;

    ReflectionSerializer(final SerializerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public byte[] serialize(final Object input) {
        checkNotNull(input);
        final Field[] fields = input.getClass().getDeclaredFields();
        final StringBuilder builder = new StringBuilder();
        // Guarantees fields that were registered are used.
        for (final Field field : fields) {
            field.setAccessible(true);
            try {
                final Object fieldValue = field.get(input);
                builder.append(field.getName()).append(" = ")
                        .append(StringEncoding.get().decode(serializeFieldValue(fieldValue)))
                        .append("\n");
            } catch (final IllegalAccessException e) {
                // This shouldn't happen because we set accessible to true.
                throw new GoldFileException("Couldn't access field " + field.getName());
            }
        }

        return StringEncoding.get().encode(builder.toString());
    }

    public byte[] serializeFieldValue(final Object fieldValue) {
        final Serializer fieldValueSerializer = Optional.ofNullable(fieldValue)
                .flatMap(fValue -> registry.getUnsafe(fValue.getClass()))
                .orElseGet(() -> new ToStringSerializer(registry));
        return fieldValueSerializer.serialize(fieldValue);
    }
}
