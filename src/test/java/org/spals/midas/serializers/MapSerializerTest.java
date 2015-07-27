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
        String actual = Converter.fromUtf8(
            new MapSerializer(
                 SerializerMap.make()
                    .put(String.class, Serializers.STRING)
                    .put(Integer.class, Serializers.INTEGER)
            ).serialize(map)
        );
        assertThat(actual, is("(foo->1)"));
    }
}