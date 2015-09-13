package net.spals.midas.util;

import java.lang.reflect.Constructor;

/**
 * @author spags
 */
public class Tests {

    /**
     * Lets you call a private constructor in a factory class for test coverage.
     */
    public static void testPrivate(final Class<?> clazz) throws Exception {
        final Constructor<?>[] cons = clazz.getDeclaredConstructors();
        cons[0].setAccessible(true);
        cons[0].newInstance((Object[]) null);
    }
}
