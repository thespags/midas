package org.spals.midas.serializer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author spags
 */
public class IterableSerializerTest {

    @Test
    public void testSetSerialize() {
        final Set<String> set = ImmutableSet.of("foo", "bar");
        final String actual = new IterableSerializer(SerializerMap.make().putJava()).serialize(set);
        assertThat(actual, is("{foo, bar}"));
    }

    @Test
    public void testListSerialize() {
        final List<String> list = ImmutableList.of("foo", "bar");
        final String actual = new IterableSerializer(SerializerMap.make().putJava()).serialize(list);
        assertThat(actual, is("[foo, bar]"));
    }

    @Test
    public void testRandomIterableSerialize() {
        final Foo<String> foo = new Foo<>(ImmutableList.of("foo", "bar"));
        final String actual = new IterableSerializer(SerializerMap.make().putJava()).serialize(foo);
        assertThat(actual, is("(foo, bar)"));
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