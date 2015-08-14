package org.spals.midas.reader;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author spags
 */
public class GoldFileReaders {

    public static final GoldFileReader CLASS_PATH = new ClasspathGoldFileReader();

    public static final GoldFileReader FILE_SYSTEM = new FileSystemGoldFileReader();

    /**
     * @author tkral
     */
    private static class ClasspathGoldFileReader implements GoldFileReader {

        private InputStream inputStream;

        @Override
        public byte[] read() throws IOException {
            try (final InputStream is = inputStream) {
                return ByteStreams.toByteArray(is);
            }
        }

        @Override
        public boolean exists(final String location) {
            final ClassLoader loader = ClasspathGoldFileReader.class.getClassLoader();
            inputStream = loader.getResourceAsStream(location);
            return inputStream != null;
        }
    }

    /**
     * @author tkral
     */
    private static class FileSystemGoldFileReader implements GoldFileReader {

        private File file;

        @Override
        public byte[] read() throws IOException {
            return Files.readAllBytes(file.toPath());
        }

        @Override
        public boolean exists(final String location) {
            file = new File(checkNotNull(location));

            return file.exists();
        }
    }
}
