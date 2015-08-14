package org.spals.midas.serializers;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PrimitiveArraySerializerTest {

    @Test
    public void testSerialize() {
        final int[] array = new int[]{1, 2, 3};
        final String actual = new PrimitiveArraySerializer(SerializerMap.make().putJava()).serialize(array);
        assertThat(actual, is("[1, 2, 3]"));
    }
}