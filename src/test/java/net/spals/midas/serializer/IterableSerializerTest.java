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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static net.spals.midas.serializer.ByteMatcher.bytes;

/**
 * @author spags
 */
public class IterableSerializerTest {

    @Test
    public void testSetSerialize() {
        final Set<String> set = ImmutableSet.of("foo", "bar");
        final byte[] actual = new IterableSerializer(SerializerRegistry.newDefault()).serialize(set);
        assertThat(actual, bytes("{foo, bar}"));
    }

    @Test
    public void testListSerialize() {
        final List<String> list = ImmutableList.of("foo", "bar");
        final byte[] actual = new IterableSerializer(SerializerRegistry.newDefault()).serialize(list);
        assertThat(actual, bytes("[foo, bar]"));
    }

    @Test
    public void testRandomIterableSerialize() {
        final Foo<String> foo = new Foo<>(ImmutableList.of("foo", "bar"));
        final byte[] actual = new IterableSerializer(SerializerRegistry.newDefault()).serialize(foo);
        assertThat(actual, bytes("(foo, bar)"));
    }

    /**
     * Mask a list, so we can test the bracket behavior for a non list, non set iterable.
     */
    private static class Foo<T> implements Iterable<T> {

        private final List<T> list;

        public Foo(final List<T> list) {
            this.list = list;
        }

        @Override
        public Iterator<T> iterator() {
            return list.iterator();
        }
    }
}