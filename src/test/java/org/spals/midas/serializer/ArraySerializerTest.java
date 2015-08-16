package org.spals.midas.serializer;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ArraySerializerTest {

    @Test
    public void testSerialize() {
        final Integer[] array = new Integer[]{1, 2, 3};
        final String actual = new ArraySerializer<Integer>(SerializerMap.make().putJava()).serialize(array);
        assertThat(actual, is("[1, 2, 3]"));
    }
}