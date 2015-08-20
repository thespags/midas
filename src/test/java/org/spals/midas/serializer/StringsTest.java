package org.spals.midas.serializer;

import org.hamcrest.Matchers;
import org.spals.midas.util.Tests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.spals.midas.serializer.ByteMatcher.bytes;

/**
 * @author spags
 */
public class StringsTest {

    public static final String FOO = "foo";
    public static final byte[] BYTES = FOO.getBytes();

    @BeforeMethod
    public void setUp() {
        Strings.BYTES_TO_STRING.clear();
        Strings.STRING_TO_BYTES.clear();
    }

    @Test
    public void testPrivate() throws Exception {
        Tests.testPrivate(Strings.class);
    }

    @Test
    public void testDecode() throws Exception {
        final String decode = Strings.decode(BYTES);
        assertThat(decode, Matchers.is(FOO));
        assertThat(Strings.BYTES_TO_STRING, hasEntry(BYTES, FOO));
        assertThat(Strings.STRING_TO_BYTES, hasEntry(FOO, BYTES));
    }

    @Test
    public void testEncode() throws Exception {
        final byte[] encode = Strings.encode(FOO);
        assertThat(encode, bytes(FOO));
        assertThat(Strings.BYTES_TO_STRING, hasEntry(BYTES, FOO));
        assertThat(Strings.STRING_TO_BYTES, hasEntry(FOO, BYTES));
    }
}