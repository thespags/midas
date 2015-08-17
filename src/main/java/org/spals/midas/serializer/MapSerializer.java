package org.spals.midas.serializer;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * For a given {@link Map}<K, V>, this will use the {@link Serializer} provided for K, V to serialize.
 * <br>This will be represented as (serialize(K) => serialize(V), ...)
 *
 * @author spags
 */
class MapSerializer implements Serializer<Map> {

    private final SerializerMap serializers;

    public MapSerializer(final SerializerMap serializers) {
        this.serializers = serializers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String serialize(final Map map) {
        return StreamSupport.stream(((Map<?, ?>) map).entrySet().spliterator(), false)
            .map(
                entry ->
                    serializers.getUnsafe(entry.getKey().getClass()).serialize(entry.getKey())
                        + "->"
                        + serializers.getUnsafe(entry.getValue().getClass()).serialize(entry.getValue())
            )
            .collect(Collectors.joining(", ", "(", ")"));
    }
}
