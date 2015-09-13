package net.spals.midas.serializer;

/**
 * Calls {@link #toString()} for a given type T.
 * If the input is null then will return a string of {@code "<null>" }
 *
 * @author spags
 */
class ToStringSerializer<T> implements Serializer<T> {

    @Override
    public byte[] serialize(final T input) {
        return input == null
            ? Strings.encode(Strings.NULL)
            : Strings.encode(input.toString());
    }
}