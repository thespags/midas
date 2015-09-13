package net.spals.midas.serializer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * For a given {@link Iterable}&lt;T&gt;, this will use the {@link Serializer} provided for T to serialize.
 * <br>Sets will be marked with {, }
 * <br>Lists will be marked with [, ]
 * <br>Any other iterable will be marked with (, )
 *
 * @author spags
 */
class IterableSerializer implements Serializer<Iterable> {

    private final SerializerMap serializers;

    public IterableSerializer(final SerializerMap serializers) {
        this.serializers = serializers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize(final Iterable iterable) {
        final Collector<CharSequence, ?, String> joiner;
        if (iterable instanceof Set) {
            joiner = Collectors.joining(", ", "{", "}");
        } else if (iterable instanceof List) {
            joiner = Collectors.joining(", ", "[", "]");
        } else {
            joiner = Collectors.joining(", ", "(", ")");
        }
        return Strings.encode(
            StreamSupport.stream(((Iterable<?>) iterable).spliterator(), false)
                .map(v -> Strings.decode(serializers.getUnsafe(v.getClass()).serialize(v)))
                .collect(joiner)
        );
    }
}
