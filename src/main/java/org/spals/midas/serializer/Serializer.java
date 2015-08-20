package org.spals.midas.serializer;

/**
 * Returns a string value from a given input
 *
 * @author spags
 */
public interface Serializer<T> {
    byte[] serialize(T input);
}
