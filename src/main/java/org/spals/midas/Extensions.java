package org.spals.midas;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility for adding the midas extension.
 *
 * @author spags
 */
class Extensions {

    private Extensions() {
    }

    public static Path add(final Path path, final String ext) {
        final String fileName = path.getFileName().toString();
        final int extensionIndex = fileName.lastIndexOf('.');

        final Path subPath = path.getNameCount() > 1 ? path.subpath(0, path.getNameCount() - 1) : Paths.get("");

        if (extensionIndex == -1) {
            return subPath.resolve(fileName + "." + ext);
        }

        final String name = fileName.substring(0, extensionIndex);
        final String extension = fileName.substring(extensionIndex);

        return subPath.resolve(name + "." + ext + extension);
    }
}
