package org.spals.midas.serializers;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.spals.midas.serializers.Converter.fromUtf8;
import static org.spals.midas.serializers.Converter.toUtf8;

class IterableSerializer implements Serializer<Iterable> {

    private final SerializerMap serializers;

    public IterableSerializer(final SerializerMap serializers) {
        Preconditions.checkNotNull(serializers);
        this.serializers = serializers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize(Iterable iterable) {
        Collector<CharSequence, ?, String> joiner;
        if (iterable instanceof Set) {
            joiner = Collectors.joining(", ", "{", "}");
        } else if (iterable instanceof List) {
            joiner = Collectors.joining(", ", "[", "]");
        } else {
            joiner = Collectors.joining(", ", "(", ")");
        }

        return toUtf8(
            StreamSupport.stream(((Iterable<?>) iterable).spliterator(), false)
                .map(v -> fromUtf8(serializers.getUnsafe(v.getClass()).serialize(v)))
                .collect(joiner)
        );
    }
}
