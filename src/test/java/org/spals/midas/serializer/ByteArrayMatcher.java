package org.spals.midas.serializer;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class ByteArrayMatcher extends TypeSafeDiagnosingMatcher<byte[]> {

    private final String expected;

    public ByteArrayMatcher(final String expected) {
        this.expected = expected;
    }

    public static Matcher<byte[]> isBytes(final String expected) {
        return new ByteArrayMatcher(expected);
    }

    @Override
    protected boolean matchesSafely(byte[] bytes, Description description) {
        final String actual = Converter.fromUtf8(bytes);
        description.appendText("was ").appendText(actual);
        return actual.equals(expected);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expected);
    }
}
