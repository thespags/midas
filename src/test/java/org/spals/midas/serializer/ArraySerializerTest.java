package org.spals.midas.serializer;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.spals.midas.serializer.ByteMatcher.bytes;

/**
 * @author spags
 */
public class ArraySerializerTest {

    @Test
    public void testSerialize() {
        final Integer[] array = new Integer[]{1, 2, 3};
        final byte[] actual = new ArraySerializer<Integer>(SerializerMap.make().putJava()).serialize(array);
        assertThat(actual, bytes("[1, 2, 3]"));
    }
}