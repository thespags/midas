package org.spals.midas.serializers;

/**
 * Calls {@link #toString()} for a given type T.
 */
class ToStringSerializer<T> implements Serializer<T> {
    @Override
    public String serialize(final T input) {
        return input.toString();
    }
}