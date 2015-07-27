package org.spals.midas.serializers;

import java.nio.charset.StandardCharsets;

public class Converter {

    private Converter() {
    }

    public static String fromUtf8(final byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] toUtf8(final String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }
}
