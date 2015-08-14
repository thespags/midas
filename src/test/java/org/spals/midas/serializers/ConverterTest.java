package org.spals.midas.serializers;

import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ConverterTest {

    @Test
    public void testTo() {
        final String string = "foo";
        assertThat(Converter.toUtf8(string), equalTo(string.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testFrom() {
        final byte[] bytes = new byte[]{1, 2, 3};
        assertThat(Converter.fromUtf8(bytes), equalTo(new String(bytes, StandardCharsets.UTF_8)));
    }
}