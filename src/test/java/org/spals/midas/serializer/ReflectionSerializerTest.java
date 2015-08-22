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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionHamcrestMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.spals.midas.serializer.ByteMatcher.bytes;

/**
 * @author spags
 */
public class ReflectionSerializerTest {

    @Test
    public void testSerialize() {
        final byte[] actual = ReflectionSerializer.builder()
            .registerJava()
            .build()
            .serialize(new Foo());
        final String expected =
            "littleInt = 0\n" +
                "bigInt = 1\n" +
                "littleChar = \u0002\n" +
                "bigChar = \u0003\n" +
                "littleLong = 4\n" +
                "bigLong = 5\n" +
                "littleDouble = 6.0\n" +
                "bigDouble = 6.0\n" +
                "littleFloat = 7.0\n" +
                "bigFloat = 8.0\n" +
                "littleShort = 9\n" +
                "bigShort = 10\n" +
                "littleBoolean = true\n" +
                "bigBoolean = false\n" +
                "string = foo\n" +
                "intArray = [1, 3, 5]\n" +
                "stringSet = [a, b, c]\n" +
                "intSet = {2, 4, 6}\n" +
                "map = (foo -> 1)\n";
        assertThat(actual, bytes(expected));
    }

    @Test
    public void testRegisterField() {
        final byte[] actual = ReflectionSerializer.builder()
            .registerField("littleInt")
            .registerJava()
            .build()
            .serialize(new Foo());
        final String expected = "littleInt = 0\n";
        assertThat(actual, bytes(expected));
    }

    @Test
    public void testRegisterFields() {
        final byte[] actual = ReflectionSerializer.builder()
            .registerFields("littleInt", "bigInt")
            .registerJava()
            .build()
            .serialize(new Foo());
        final String expected =
            "littleInt = 0\n" +
                "bigInt = 1\n";
        assertThat(actual, bytes(expected));
    }

    @Test
    public void testRegisterBadField() {
        final Serializer<Foo> serializer = ReflectionSerializer.builder()
            .registerFields("nonExistentField")
            .registerJava()
            .build();
        catchException(serializer).serialize(new Foo());
        assertThat(
            caughtException(),
            allOf(
                instanceOf(IllegalStateException.class),
                hasMessage("unmatched fields: [nonExistentField]")
            )
        );
    }

    @Test
    public void testRegisterSerializer() {
        final byte[] actual = ReflectionSerializer.builder()
            .register(
                Foo.class,
                input -> Strings.encode("Foo Class serializer")
            )
            .build()
            .serialize(new Default());
        assertThat(actual, bytes("foo = Foo Class serializer\n"));
    }

    @Test
    public void testDefaultSerializer() {
        final byte[] actual = ReflectionSerializer.builder()
            .registerDefault(Serializers.of())
            .build()
            .serialize(new Default());
        assertThat(actual, bytes("foo = Foo\n"));
    }

    @Test
    public void testWriteNull() {
        final byte[] actual = ReflectionSerializer.builder()
            .registerDefault(Serializers.of())
            .writeNull()
            .build()
            .serialize(new Default());
        final String value =
            "foo = Foo\n" +
                "nullFoo = <null>\n";
        assertThat(actual, bytes(value));
    }

    @Test
    public void testNoDefaultSerializer() {
        final Serializer<Default> serializer = ReflectionSerializer.builder()
            .build();
        catchException(serializer).serialize(new Default());
        assertThat(
            caughtException(),
            allOf(
                instanceOf(NullPointerException.class),
                hasMessage("missing serializer: class org.spals.midas.serializer.ReflectionSerializerTest$Foo")
            )
        );
    }

    @SuppressWarnings("unused")
    private static class Default {
        private final Foo foo = new Foo();
        private final Foo nullFoo = null;
    }

    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private static class Foo {
        private final int littleInt = 0;
        private final Integer bigInt = 1;
        private final char littleChar = 2;
        private final Character bigChar = 3;
        private final long littleLong = 4;
        private final Long bigLong = 5L;
        private final double littleDouble = 6;
        private final Double bigDouble = 6.0;
        private final float littleFloat = 7;
        private final Float bigFloat = 8F;
        private final short littleShort = 9;
        private final Short bigShort = 10;
        private final boolean littleBoolean = true;
        private final Boolean bigBoolean = false;
        private final String string = "foo";

        private final int[] intArray = new int[]{1, 3, 5};
        private final List<String> stringSet = ImmutableList.of("a", "b", "c");
        // make sure the order of the set is consistent.
        private final Set<Integer> intSet = Sets.newLinkedHashSet(Arrays.asList(2, 4, 6));
        private final Map<String, Integer> map = ImmutableMap.of("foo", 1);

        public String toString() {
            return "Foo";
        }
    }
}