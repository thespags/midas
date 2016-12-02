package net.spals.midas.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

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

    JacksonSerializer(final String fileExtension,
                      final ObjectMapper objectMapper) {
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
        try {
            return objectMapper.writeValueAsBytes(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
