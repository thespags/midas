package org.spals.midas.serializer;

/**
 * Calls {@link #toString()} for a given type T.
 *
 * @author spags
 */
class ToStringSerializer<T> implements Serializer<T> {
    @Override
    public String serialize(final T input) {
        return input.toString();
    }
}