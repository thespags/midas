package org.spals.midas.util;

import java.lang.reflect.Constructor;

public class Tests {

    public static void testPrivate(final Class<?> clazz) throws Exception {
        final Constructor<?>[] cons = clazz.getDeclaredConstructors();
        cons[0].setAccessible(true);
        cons[0].newInstance((Object[]) null);
    }
}
