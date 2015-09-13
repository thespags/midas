package net.spals.midas.serializer;

import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author spags
 */
public class PrimitiveArraySerializerTest {

    @Test
    public void testSerialize() {
        final int[] array = new int[]{1, 2, 3};
        final byte[] actual = new PrimitiveArraySerializer(SerializerMap.make().putJava()).serialize(array);
        MatcherAssert.assertThat(actual, ByteMatcher.bytes("[1, 2, 3]"));
    }
}