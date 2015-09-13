package net.spals.midas.serializer;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * Handles primitive arrays which can be handled by the generic {@link ArraySerializer}.
 *
 * @author spags
 */
class PrimitiveArraySerializer implements Serializer<Object> {

    private final SerializerMap serializers;

    public PrimitiveArraySerializer(final SerializerMap serializers) {
        Objects.requireNonNull(serializers, "bad serializer map");
        this.serializers = serializers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize(final Object value) {
        final StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < Array.getLength(value); i++) {
            if (builder.length() > 1) {
                builder.append(", ");
            }
            final Object o = Array.get(value, i);
            builder.append(Strings.decode(serializers.getUnsafe(o.getClass()).serialize(o)));
        }
        builder.append("]");
        return Strings.encode(builder.toString());
    }
}