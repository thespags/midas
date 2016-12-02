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

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Attempts some basic interning for encoding, decoding strings as a two {@link Cache}'s that expire after a few seconds.
 * The performance is probably irrelevant for the use case but it was a thought expirament to use guava's {@link Cache}.
 *
 * @author spags
 */
final class Strings {

    static final String NULL = "<null>";
    private static final Strings INSTANCE = new Strings(Ticker.systemTicker());
    private final Cache<String, byte[]> stringToBytes;
    private final Cache<byte[], String> bytesToString;

    Strings(final Ticker ticker) {
        stringToBytes = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .ticker(ticker)
            .build();
        bytesToString = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .ticker(ticker)
            .build();
    }

    static Strings get() {
        return INSTANCE;
    }

    synchronized String decode(final byte[] bytes) {
        final String value;
        try {
            value = bytesToString.get(bytes, () -> new String(bytes, StandardCharsets.UTF_8));
            stringToBytes.put(value, bytes);
            return value;
        } catch (final ExecutionException x) {
            throw new RuntimeException(x);
        }
    }

    synchronized byte[] encode(final String string) {
        final byte[] value;
        try {
            value = stringToBytes.get(string, string::getBytes);
            bytesToString.put(value, string);
            return value;
        } catch (final ExecutionException x) {
            throw new RuntimeException(x);
        }
    }

    Cache<String, byte[]> getStringToBytes() {
        return stringToBytes;
    }

    Cache<byte[], String> getBytesToString() {
        return bytesToString;
    }
}
