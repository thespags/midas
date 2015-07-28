package org.spals.midas.reader;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author tkral
 */
public class ClasspathGoldFileReader implements GoldFileReader {

    @Override
    public byte[] readGoldFile(final String goldFileLocation) throws IOException {
        final ClassLoader cl = ClasspathGoldFileReader.class.getClassLoader();
        try (final InputStream is = cl.getResourceAsStream(checkNotNull(goldFileLocation))) {
            checkNotNull(is, String.format("Could not find gold file on classpath: %s", goldFileLocation));

            return ByteStreams.toByteArray(is);
        }
    }
}
