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

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ticker;
import com.google.common.cache.*;

/**
 * Common utilities for encoding and decoding {@link String}s.
 * <p>
 * This includes a small {@link Cache} to speed up the process for
 * commonly encoded / decoded values.
 *
 * @author spags
 * @author tkral
 */
final class StringEncoding {

    static final String NULL = "<null>";
    private static final StringEncoding INSTANCE = new StringEncoding(Ticker.systemTicker());
    private final LoadingCache<String, byte[]> stringToBytesCache;
    private final LoadingCache<byte[], String> bytesToStringCache;

    @VisibleForTesting
    StringEncoding(final Ticker ticker) {
        stringToBytesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .ticker(ticker)
            .build(new StringToBytesCacheLoader());
        bytesToStringCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .ticker(ticker)
            .build(new BytesToStringCacheLoader());
    }

    static StringEncoding get() {
        return INSTANCE;
    }

    String decode(final byte[] bytes) {
        try {
            final String value = bytesToStringCache.get(bytes);
            stringToBytesCache.put(value, bytes);
            return value;
        } catch (final ExecutionException x) {
            throw new RuntimeException(x);
        }
    }

    byte[] encode(final String string) {
        try {
            final byte[] value = stringToBytesCache.get(string);
            bytesToStringCache.put(value, string);
            return value;
        } catch (final ExecutionException x) {
            throw new RuntimeException(x);
        }
    }

    @VisibleForTesting
    Cache<String, byte[]> getStringToBytesCache() {
        return stringToBytesCache;
    }

    @VisibleForTesting
    Cache<byte[], String> getBytesToStringCache() {
        return bytesToStringCache;
    }

    /**
     * A {@link CacheLoader} to convert a byte array
     * to a {@link String}.
     *
     * @author tkral
     */
    private final static class BytesToStringCacheLoader extends CacheLoader<byte[], String> {

        @Override
        public String load(final byte[] key) throws Exception {
            return new String(key, StandardCharsets.UTF_8);
        }
    }

    /**
     * A {@link CacheLoader} to convert a {@link String}
     * to a byte array.
     *
     * @author tkral
     */
    private final static class StringToBytesCacheLoader extends CacheLoader<String, byte[]> {

        @Override
        public byte[] load(final String key) throws Exception {
            return key.getBytes();
        }
    }
}
