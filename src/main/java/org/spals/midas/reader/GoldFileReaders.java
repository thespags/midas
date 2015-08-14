package org.spals.midas.reader;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author spags
 */
public final class GoldFileReaders {

    public static GoldFileReader classPathReader(final String location) {
        final InputStream inputStream = GoldFileReaders.class.getClassLoader().getResourceAsStream(location);
        return new ClasspathGoldFileReader(inputStream);
    }

    public static GoldFileReader fileSystemReader(final String location) {
        final File file = new File(location);
        return new FileSystemGoldFileReader(file);
    }

    /**
     * @author tkral
     */
    private static class ClasspathGoldFileReader implements GoldFileReader {

        private final InputStream inputStream;

        @VisibleForTesting
        ClasspathGoldFileReader(final InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public byte[] read() throws IOException {
            try (final InputStream is = inputStream) {
                return ByteStreams.toByteArray(is);
            }
        }

        @Override
        public boolean exists() {
            return inputStream != null;
        }
    }

    /**
     * @author tkral
     */
    private static class FileSystemGoldFileReader implements GoldFileReader {

        private final File file;

        @VisibleForTesting
        FileSystemGoldFileReader(final File file) {
            this.file = file;
        }

        @Override
        public byte[] read() throws IOException {
            return Files.readAllBytes(file.toPath());
        }

        @Override
        public boolean exists() {
            return file.exists();
        }
    }
}
