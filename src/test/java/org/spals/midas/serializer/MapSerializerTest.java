package org.spals.midas.serializer;

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author spags
 */
public class MapSerializerTest {

    @Test
    public void testSerialize() {
        final Map<String, Integer> map = ImmutableMap.of("foo", 1);
        final String actual = new MapSerializer(SerializerMap.make().putJava()).serialize(map);
        assertThat(actual, is("(foo->1)"));
    }
}