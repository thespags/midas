/*
 * Copyright (c) 2015, James T Spagnola & Timothy P Kral
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.spals.midas.io;

import net.spals.midas.GoldFileException;

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
