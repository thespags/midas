package org.spals.midas.serializers;

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MapSerializerTest {

    @Test
    public void testSerialize() {
        Map<String, Integer> map = ImmutableMap.of("foo", 1);
        // TODO add a byte[] matcher
        String actual = Converter.fromUtf8(new MapSerializer(SerializerMap.make().putJava()).serialize(map));
        assertThat(actual, is("(foo->1)"));
    }
}