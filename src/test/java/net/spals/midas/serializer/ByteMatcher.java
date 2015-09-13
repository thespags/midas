package net.spals.midas.serializer;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Arrays;

/**
 * Matcher for byte arrays.
 *
 * @author spags
 */
public class ByteMatcher extends TypeSafeDiagnosingMatcher<byte[]> {

    private final String expected;

    public ByteMatcher(final String expected) {
        this.expected = expected;
    }

    public static ByteMatcher bytes(final String expected) {
        return new ByteMatcher(expected);
    }

    @Override
    protected boolean matchesSafely(final byte[] bytes, final Description description) {
        description.appendText(Strings.decode(bytes));
        return Arrays.equals(bytes, Strings.encode(expected));
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(expected);
    }
}
