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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.concurrent.TimeUnit;

import com.google.common.testing.FakeTicker;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link StringEncoding}.
 *
 * @author spags
 * @author tkral
 */
public class StringEncodingTest {

    private static final String FOO = "foo";
    private static final byte[] BYTES = FOO.getBytes();
    private StringEncoding stringEncoding;
    private FakeTicker ticker;

    @BeforeMethod
    public void setUp() {
        ticker = new FakeTicker();
        stringEncoding = new StringEncoding(ticker);
    }

    @Test
    public void testCacheCleanUp() throws Exception {
        stringEncoding.encode(FOO);
        // expire the cache information and run clean up
        ticker.advance(6, TimeUnit.SECONDS);
        stringEncoding.getBytesToStringCache().cleanUp();
        stringEncoding.getStringToBytesCache().cleanUp();

        assertThat(stringEncoding.getBytesToStringCache().asMap(), anEmptyMap());
        assertThat(stringEncoding.getStringToBytesCache().asMap(), anEmptyMap());
    }

    @Test
    public void testDecode() throws Exception {
        final String decode = stringEncoding.decode(BYTES);
        assertThat(decode, is(FOO));
        assertThat(stringEncoding.getBytesToStringCache().asMap(), hasEntry(BYTES, FOO));
        assertThat(stringEncoding.getStringToBytesCache().asMap(), hasEntry(FOO, BYTES));
    }

    @Test
    public void testEncode() throws Exception {
        final byte[] encode = stringEncoding.encode(FOO);
        assertThat(encode, ByteMatcher.bytes(FOO));
        assertThat(stringEncoding.getBytesToStringCache().asMap(), hasEntry(BYTES, FOO));
        assertThat(stringEncoding.getStringToBytesCache().asMap(), hasEntry(FOO, BYTES));
    }
}