package org.spals.midas.serializers;

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.spals.midas.serializers.ByteArrayMatcher.isBytes;

public class MapSerializerTest {

    @Test
    public void testSerialize() {
        final Map<String, Integer> map = ImmutableMap.of("foo", 1);
        final byte[] actual = new MapSerializer(SerializerMap.make().putJava()).serialize(map);
        assertThat(actual, isBytes("(foo->1)"));
    }
}