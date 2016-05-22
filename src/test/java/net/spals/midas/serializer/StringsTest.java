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

import net.spals.midas.util.Tests;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.ref.WeakReference;
import java.util.Objects;

import static net.spals.midas.serializer.StringsTest.WeakReferenceMatcher.withWeakReference;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

/**
 * @author spags
 */
public class StringsTest {

    private static final String FOO = "foo";
    private static final byte[] BYTES = FOO.getBytes();

    @BeforeMethod
    public void setUp() {
        Strings.BYTES_TO_STRING.clear();
        Strings.STRING_TO_BYTES.clear();
    }

    @Test
    public void testPrivate() throws Exception {
        Tests.testPrivate(Strings.class);
    }

    @Test
    public void testDecode() throws Exception {
        final String decode = Strings.decode(BYTES);
        assertThat(decode, is(FOO));
        assertThat(Strings.BYTES_TO_STRING, hasEntry(is(BYTES), withWeakReference(FOO)));
        assertThat(Strings.STRING_TO_BYTES, hasEntry(is(FOO), withWeakReference(BYTES)));
    }

    @Test
    public void testEncode() throws Exception {
        final byte[] encode = Strings.encode(FOO);
        assertThat(encode, ByteMatcher.bytes(FOO));
        assertThat(Strings.BYTES_TO_STRING, hasEntry(is(BYTES), withWeakReference(FOO)));
        assertThat(Strings.STRING_TO_BYTES, hasEntry(is(FOO), withWeakReference(BYTES)));
    }

    static class WeakReferenceMatcher<T> extends TypeSafeDiagnosingMatcher<WeakReference<T>> {

        private final T value;

        private WeakReferenceMatcher(final T value) {
            this.value = value;
        }

        static <T> WeakReferenceMatcher<T> withWeakReference(final T value) {
            return new WeakReferenceMatcher<>(value);
        }

        @Override
        protected boolean matchesSafely(final WeakReference<T> item, final Description mismatchDescription) {
            if (item == null) {
                mismatchDescription.appendText("no reference");
                return false;
            }
            return Objects.deepEquals(value, item.get());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendValue(value);
        }
    }
}