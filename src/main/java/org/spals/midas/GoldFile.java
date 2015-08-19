package org.spals.midas;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.spals.midas.io.FileUtil;
import org.spals.midas.io.GoldPath;
import org.spals.midas.io.GoldPaths;
import org.spals.midas.serializer.ReflectionSerializer;
import org.spals.midas.serializer.Serializer;
import org.spals.midas.serializer.Serializers;
import org.spals.midas.util.VisibleForTesting;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Creates an algorithm to diff an object against a source gold file reported the results.
 * The file will be stored in {file}.midas.
 * Default serializer is a {@link #toString()} of {@link T}.
 * Default path is {@link GoldPaths#simple()}
 *
 * @author spags, tkral
 */
public class GoldFile<T> {

    private static final Logger LOGGER = Logger.getLogger(GoldFile.class.getName());
    private static final String EXT = ".midas";

    private final Serializer<T> serializer;
    private final GoldPath path;
    private final FileUtil files;

    private GoldFile(final Builder<T> builder) {
        this.path = builder.path;
        this.serializer = builder.serializer;
        this.files = builder.files;
    }

    public static <T> GoldFile.Builder<T> builder() {
        return new GoldFile.Builder<>();
    }

    public void run(final T object, final String fileName) {

        final Path file = path.get(Paths.get(fileName + EXT));

        files.makeParents(file);
        files.createFile(file);

        final String newString = serializer.serialize(object);
        final byte[] newBytes = newString.getBytes(StandardCharsets.UTF_8);

        final byte[] oldBytes = files.readAllBytes(file);
        final String oldString = new String(oldBytes, StandardCharsets.UTF_8);

        try {
            if (!Arrays.equals(oldBytes, newBytes)) {
                final String diff = diff(newString, oldString);
                throw new GoldFileException(String.format("\nDiffs: %s\n%s", file, diff));
            }

            final boolean updatedTimeStamp = files.setLastModified(file, System.currentTimeMillis());
            if (!updatedTimeStamp) {
                LOGGER.warning("Unable to update last modification date: " + path);
            }
        } finally {
            files.write(file, newBytes);
        }
    }

    /**
     * Uses {@link DiffUtils} to properly determine the differences between the old and new data.
     */
    private String diff(final String newString, final String oldString) {
        final String[] newStrings = newString.split("\n");
        final String[] oldStrings = oldString.split("\n");
        final Patch<String> patch = DiffUtils.diff(Arrays.asList(oldStrings), Arrays.asList(newStrings));

        final StringBuilder builder = new StringBuilder();

        for (final Delta<String> delta : patch.getDeltas()) {
            switch (delta.getType()) {
                case CHANGE:
                    chunk(builder, delta.getOriginal(), " << ");
                    chunk(builder, delta.getRevised(), " >> ");
                    break;
                case DELETE:
                    chunk(builder, delta.getOriginal(), " << ");
                    break;
                case INSERT:
                    chunk(builder, delta.getRevised(), " >> ");
                    break;
            }
        }
        return builder.toString();
    }

    private void chunk(final StringBuilder builder, final Chunk<String> chunk, final String dir) {
        builder.append(String.format("%s", chunk.getPosition()))
            .append(dir)
            .append(StreamSupport.stream(chunk.getLines().spliterator(), false).collect(Collectors.joining("\n")))
            .append("\n");
    }

    public final static class Builder<T> {

        private Serializer<T> serializer;
        private GoldPath path;
        private FileUtil files;

        private Builder() {
            this.path = GoldPaths.simple();
            this.serializer = Serializers.of();
            this.files = new FileUtil();
        }

        @VisibleForTesting
        Builder<T> withFileUtil(final FileUtil files) {
            this.files = files;
            return this;
        }

        public Builder<T> withPath(final GoldPath path) {
            this.path = path;
            return this;
        }

        /**
         * Basic reflection serializer using default built in serializers for java types.
         */
        public Builder<T> withReflectionSerializer() {
            return withSerializer(ReflectionSerializer.builder().registerJava().build());
        }

        public Builder<T> withSerializer(final Serializer<T> serializer) {
            Objects.requireNonNull(serializer, "bad serializer");
            this.serializer = serializer;
            return this;
        }

        public GoldFile<T> build() {
            return new GoldFile<>(this);
        }
    }
}
