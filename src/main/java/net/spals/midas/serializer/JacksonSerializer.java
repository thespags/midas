package net.spals.midas.serializer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A {@link Serializer} implementation
 * which uses the Jackson library for
 * data serialization.
 *
 * @author tkral
 */
class JacksonSerializer implements Serializer {

    private final String fileExtension;
    private final ObjectMapper objectMapper;

    JacksonSerializer(
        final String fileExtension,
        final ObjectMapper objectMapper
    ) {
        this.fileExtension = fileExtension;
        this.objectMapper = objectMapper;
    }

    /**
     * @see Serializer#fileExtension()
     */
    @Override
    public String fileExtension() {
        return fileExtension;
    }

    /**
     * @see Serializer#serialize(Object)
     */
    @Override
    public byte[] serialize(final Object input) {
        return Optional.ofNullable(input)
            .map(this::jacksonSerialize)
            .orElseGet(() -> StringEncoding.get().encode(StringEncoding.NULL));
    }

    private byte[] jacksonSerialize(final Object input) {
        checkNotNull(input);
        try {
            return objectMapper.writeValueAsBytes(input);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
