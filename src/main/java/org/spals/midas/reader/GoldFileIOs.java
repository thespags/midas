package org.spals.midas.reader;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import org.spals.midas.GoldFileException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author spags
 */
public final class GoldFileIOs {

    public static GoldFileIO classPath(final Class<?> clazz, final String location) {
        final InputStream inputStream = clazz.getClassLoader().getResourceAsStream(location);
        return new ClassPathGoldFileIO(clazz, location, inputStream);
    }

    public static GoldFileIO fileSystem(final String location) {
        final File file = new File(location);
        return new FileSystemGoldFileIO(location, file);
    }

    private GoldFileIOs() {
    }

    /**
     * @author tkral
     */
    private static class ClassPathGoldFileIO implements GoldFileIO {

        private final Class<?> clazz;
        private final String location;
        private final InputStream inputStream;

        @VisibleForTesting
        ClassPathGoldFileIO(final Class<?> clazz, final String location, final InputStream inputStream) {
            this.clazz = clazz;
            this.location = location;
            this.inputStream = inputStream;
        }

        @Override
        public byte[] read() {
            try (final InputStream stream = inputStream) {
                return ByteStreams.toByteArray(stream);
            } catch (final IOException x) {
                throw new GoldFileException(location + " could not be read", x);
            }
        }

        @Override
        public boolean exists() {
            return inputStream != null;
        }

        @Override
        public void create() {
            final URL url = clazz.getClassLoader().getResource(location);
            if (url == null) {
                throw new GoldFileException(location + " could not be created");
            }
            createFile(new File(url.getPath()));
        }

        @Override
        public void write(final byte[] newBytes) {
            final URL url = clazz.getClassLoader().getResource(location);
            if (url == null) {
                throw new GoldFileException(location + " could not be written");
            }
            writeFile(new File(url.getPath()), newBytes);
        }
    }

    /**
     * @author tkral
     */
    private static class FileSystemGoldFileIO implements GoldFileIO {

        private final String location;
        private final File file;

        @VisibleForTesting
        FileSystemGoldFileIO(final String location, final File file) {
            this.location = location;
            this.file = file;
        }

        @Override
        public byte[] read() {
            try {
                return Files.readAllBytes(file.toPath());
            } catch (final IOException x) {
                throw new GoldFileException(location + " could not be read", x);
            }
        }

        @Override
        public boolean exists() {
            return file.exists();
        }

        @Override
        public void create() {
            createFile(file);
        }

        @Override
        public void write(final byte[] newBytes) {
            writeFile(file, newBytes);
        }
    }

    private static void writeFile(final File file, final byte[] newBytes) {
        try {
            Files.write(file.toPath(), newBytes);
        } catch (final IOException x) {
            throw new GoldFileException(file.getAbsolutePath() + " could not be written", x);
        }

    }

    private static void createFile(final File file) {
        if (!file.mkdirs()) {
            throw new GoldFileException(file.getAbsolutePath() + " could not be created");
        }
        try {
            if (!file.createNewFile()) {
                throw new GoldFileException(file.getAbsolutePath() + " could not be created");
            }
        } catch (final IOException x) {
            throw new GoldFileException(file.getAbsolutePath() + " could not be created", x);
        }
    }
}
