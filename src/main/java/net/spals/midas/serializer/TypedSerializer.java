package net.spals.midas.serializer;

/**
 * @author tkral
 */
@FunctionalInterface
public interface TypedSerializer<T> extends Serializer {

    @SuppressWarnings("unchecked")
    default byte[] serialize(final Object input) {
        return typedSerialize((T) input);
    }

    byte[] typedSerialize(T input);
}
