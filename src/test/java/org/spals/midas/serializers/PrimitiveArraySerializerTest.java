package org.spals.midas.serializers;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.spals.midas.serializers.ByteArrayMatcher.isBytes;

public class PrimitiveArraySerializerTest {

    @Test
    public void testSerialize() {
        final int[] array = new int[]{1, 2, 3};
        final byte[] actual = new PrimitiveArraySerializer(SerializerMap.make().putJava()).serialize(array);
        assertThat(actual, isBytes("[1, 2, 3]"));
    }
}