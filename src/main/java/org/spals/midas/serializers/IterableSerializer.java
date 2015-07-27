package org.spals.midas.serializers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.spals.midas.serializers.Converter.fromUtf8;
import static org.spals.midas.serializers.Converter.toUtf8;

class IterableSerializer<T> implements Serializer<Iterable<T>> {

    private final Serializer<T> element;

    public IterableSerializer(final Serializer<T> element) {
        this.element = element;
    }

    @Override
    public byte[] serialize(Iterable<T> iterable) {
        Collector<CharSequence, ?, String> joiner;
        if (iterable instanceof Set) {
            joiner = Collectors.joining(", ", "{", "}");
        } else if (iterable instanceof List) {
            joiner = Collectors.joining(", ", "[", "]");
        } else {
            joiner = Collectors.joining(", ", "(", ")");
        }
        return toUtf8(
            StreamSupport.stream(iterable.spliterator(), false)
                .map(v -> fromUtf8(element.serialize(v)))
                .collect(joiner)
        );
    }
}
