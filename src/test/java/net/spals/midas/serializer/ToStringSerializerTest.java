package net.spals.midas.serializer;

import org.testng.annotations.Test;

import static org.junit.Assert.assertThat;

/**
 * @author spags
 */
public class ToStringSerializerTest {

    @Test
    public void testSerialize() {
        assertThat(Serializers.of().serialize("foo"), ByteMatcher.bytes("foo"));
    }

    @Test
    public void testNullSerialize() {
        assertThat(Serializers.of().serialize(null), ByteMatcher.bytes("<null>"));
    }
}