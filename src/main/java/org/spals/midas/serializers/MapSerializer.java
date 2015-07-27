package org.spals.midas.serializers;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.spals.midas.serializers.Converter.fromUtf8;

class MapSerializer implements Serializer<Map> {

    private final SerializerMap serializers;

    public MapSerializer(final SerializerMap serializers) {
        this.serializers = serializers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize(Map map) {
        return Converter.toUtf8(
            StreamSupport.stream(((Map<?, ?>) map).entrySet().spliterator(), false)
                .map(
                    entry ->
                        fromUtf8(((Serializer) serializers.getUnsafe(entry.getKey().getClass())).serialize(entry.getKey()))
                        + "->" +
                        fromUtf8(((Serializer) serializers.getUnsafe(entry.getValue().getClass())).serialize(entry.getValue()))
                )
                .collect(Collectors.joining(", ", "(", ")"))
            );
    }
}
