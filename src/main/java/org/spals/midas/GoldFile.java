package org.spals.midas;

import org.spals.midas.differ.Differ;
import org.spals.midas.differ.Differs;
import org.spals.midas.io.FileUtil;
import org.spals.midas.io.GoldPath;
import org.spals.midas.io.GoldPaths;
import org.spals.midas.serializer.ReflectionSerializer;
import org.spals.midas.serializer.Serializer;
import org.spals.midas.serializer.Serializers;
import org.spals.midas.util.VisibleForTesting;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

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

    private final Serializer<T> serializer;
    private final GoldPath goldPath;
    private final Differ differ;
    private final FileUtil files;

    private GoldFile(final Builder<T> builder) {
        this.goldPath = builder.path;
        this.serializer = builder.serializer;
        this.files = builder.files;
        this.differ = builder.differ;
    }

    public static <T> GoldFile.Builder<T> builder() {
        return new GoldFile.Builder<>();
    }

    /**
     * Runs with the default values of {@link GoldOptions}
     */
    public void run(final T object, final Path path) {
        run(object, path, GoldOptions.create());
    }

    /**
     * Allows you to specify the behavior of this run with {@link GoldOptions} that are independent of this
     * particular gold file suite.
     */
    public void run(final T object, final Path path, final GoldOptions options) {
        final Path fullPath = goldPath.get(path);
        final Path file = Extensions.add(fullPath, Extensions.MIDAS_EXT);

        files.makeParents(file);
        files.createFile(file);

        // todo maybe add image, jackson, serialization?
        final byte[] newBytes = serializer.serialize(object);
        final byte[] oldBytes = files.readAllBytes(file);

        try {
            if (!Arrays.equals(oldBytes, newBytes)) {
                final String diff = differ.diff(oldBytes, newBytes);
                throw new GoldFileException(String.format("\nDiffs: %s\n%s", file, diff));
            }

            final boolean updatedTimeStamp = files.setLastModified(file, System.currentTimeMillis());
            if (!updatedTimeStamp) {
                LOGGER.warning("Unable to update last modification date: " + fullPath);
            }
        } finally {
            if (options.writable()) {
                final Path resultFile = options.checkout() ? file : Extensions.add(file, Extensions.RESULT_EXT);
                files.write(resultFile, newBytes);
            }
        }
    }

    public final static class Builder<T> {

        private Serializer<T> serializer;
        private GoldPath path;
        private FileUtil files;
        private Differ differ;

        private Builder() {
            this.path = GoldPaths.simple();
            this.serializer = Serializers.of();
            this.files = new FileUtil();
            this.differ = Differs.strings();
        }

        @VisibleForTesting
        Builder<T> withFileUtil(final FileUtil files) {
            this.files = files;
            return this;
        }

        /**
         * The parent path for this set of gold file results, by default this is empty.
         */
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

        /**
         * Assign a specific serialize to use, by default it will use a toString.
         */
        public Builder<T> withSerializer(final Serializer<T> serializer) {
            Objects.requireNonNull(serializer, "bad serializer");
            this.serializer = serializer;
            return this;
        }

        /**
         * Assign a specific differ to use, by default it will use a string differ.
         */
        public Builder<T> withDiffer(final Differ differ) {
            Objects.requireNonNull(differ, "bad differ");
            this.differ = differ;
            return this;
        }

        public GoldFile<T> build() {
            return new GoldFile<>(this);
        }
    }
}
