package org.spals.midas.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author tkral
 */
public class FilesystemGoldFileReader implements GoldFileReader {

    @Override
    public byte[] readGoldFile(final String goldFileLocation) throws IOException {
        final File goldFileFile = new File(checkNotNull(goldFileLocation));
        checkArgument(goldFileFile.exists(), String.format("Could not find gold file on file system: %s", goldFileLocation));

        return Files.readAllBytes(goldFileFile.toPath());
    }
}
