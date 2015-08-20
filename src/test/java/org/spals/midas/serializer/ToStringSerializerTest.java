package org.spals.midas.serializer;

import org.testng.annotations.Test;

import static org.junit.Assert.assertThat;
import static org.spals.midas.serializer.ByteMatcher.bytes;

/**
 * @author spags
 */
public class ToStringSerializerTest {

    @Test
    public void testSerialize() {
        assertThat(Serializers.of().serialize("foo"), bytes("foo"));
    }

    @Test
    public void testNullSerialize() {
        assertThat(Serializers.of().serialize(null), bytes("<null>"));
    }
}