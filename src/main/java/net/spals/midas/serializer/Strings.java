/*
 * Copyright (c) 2015, James T Spagnola & Timothy P Kral
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

import net.spals.midas.util.VisibleForTesting;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Attempts some basic interning for encoding, decoding strings as a bi map.
 *
 * @author spags
 */
class Strings {

    public static final String NULL = "<null>";
    @VisibleForTesting
    static final Map<String, WeakReference<byte[]>> STRING_TO_BYTES = new WeakHashMap<>();
    @VisibleForTesting
    static final Map<byte[], WeakReference<String>> BYTES_TO_STRING = new WeakHashMap<>();

    private Strings() {
    }

    public static synchronized String decode(final byte[] bytes) {
        final WeakReference<String> reference = BYTES_TO_STRING.get(bytes);

        if (reference != null) {
            final String value = reference.get();
            if (value != null) {
                return value;
            }
        }
        final String value = new String(bytes, StandardCharsets.UTF_8);
        BYTES_TO_STRING.put(bytes, new WeakReference<>(value));
        STRING_TO_BYTES.put(value, new WeakReference<>(bytes));
        return value;
    }

    public static synchronized byte[] encode(final String string) {
        final WeakReference<byte[]> reference = STRING_TO_BYTES.get(string);

        if (reference != null) {
            final byte[] value = reference.get();
            if (value != null) {
                return value;
            }
        }
        final byte[] value = string.getBytes();
        BYTES_TO_STRING.put(value, new WeakReference<>(string));
        STRING_TO_BYTES.put(string, new WeakReference<>(value));
        return value;
    }
}
