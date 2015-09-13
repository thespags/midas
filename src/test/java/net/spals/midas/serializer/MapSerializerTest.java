package net.spals.midas.serializer;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author spags
 */
public class MapSerializerTest {

    @Test
    public void testSerialize() {
        final Map<String, Integer> map = ImmutableMap.of("foo", 1);
        final byte[] actual = new MapSerializer(SerializerMap.make().putJava()).serialize(map);
        MatcherAssert.assertThat(actual, ByteMatcher.bytes("(foo -> 1)"));
    }
}