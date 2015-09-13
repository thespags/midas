package net.spals.midas.serializer;

import net.spals.midas.util.VisibleForTesting;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Attempts some basic interning for encoding, decoding strings as a bi map.
 *
 * @author spags
 */
class Strings {

    public static final String NULL = "<null>";
    @VisibleForTesting
    static final Map<String, byte[]> STRING_TO_BYTES = new WeakHashMap<>();
    @VisibleForTesting
    static final Map<byte[], String> BYTES_TO_STRING = new WeakHashMap<>();

    private Strings() {
    }

    public static String decode(final byte[] bytes) {
        String value = BYTES_TO_STRING.get(bytes);
        if (value == null) {
            value = new String(bytes, StandardCharsets.UTF_8);
            BYTES_TO_STRING.put(bytes, value);
            STRING_TO_BYTES.put(value, bytes);
        }
        return value;
    }

    public static byte[] encode(final String string) {
        byte[] value = STRING_TO_BYTES.get(string);
        if (value == null) {
            value = string.getBytes();
            STRING_TO_BYTES.put(string, value);
            BYTES_TO_STRING.put(value, string);
        }
        return value;
    }
}
