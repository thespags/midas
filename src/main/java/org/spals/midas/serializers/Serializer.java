package org.spals.midas.serializers;

/**
 * Returns a string value from a given input
 */
public interface Serializer<T> {
    String serialize(T input);
}
