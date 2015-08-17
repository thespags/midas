package org.spals.midas.io;

import org.spals.midas.GoldFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Wrapper around {@link Files} to make the calls non static so they can be easily mocked.
 *
 * @author spags
 */
public class FileUtil {

    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());

    public void write(final Path path, final byte[] value) {
        Objects.requireNonNull(path, "bad path");
        try {
            Files.write(path, value);
        } catch (final IOException x) {
            throw new GoldFileException(x);
        }
    }

    public byte[] readAllBytes(final Path path) {
        Objects.requireNonNull(path, "bad path");
        try {
            return Files.readAllBytes(path);
        } catch (final IOException x) {
            throw new GoldFileException(x);
        }
    }

    public void makeDirs(final File directory) {
        Objects.requireNonNull(directory, "bad directory");
        if (!directory.mkdirs()) {
            throw new GoldFileException("Could not create directory: " + directory);
        }
        LOGGER.info("created directory: " + directory);
    }

    public void makeParents(final Path path) {
        Objects.requireNonNull(path, "bad path");
        final File directory = path.toFile().getParentFile();
        if (directory == null || directory.exists()) {
            return;
        }
        makeDirs(directory);
    }

    public void delete(final Path path) {
        try {
            Files.delete(path);
        } catch (final IOException x) {
            throw new GoldFileException(x);
        }
    }

    public void createFile(final Path path) {
        Objects.requireNonNull(path, "bad path");
        if (!exists(path)) {
            try {
                Files.createFile(path);
            } catch (final IOException x) {
                throw new GoldFileException(x);
            }
            LOGGER.info("created file: " + path);
        }
    }

    public boolean exists(final Path path) {
        Objects.requireNonNull(path, "bad path");
        return Files.exists(path);
    }

    public boolean setLastModified(final Path path, final long timeStamp) {
        return path.toFile().setLastModified(timeStamp);
    }
}
