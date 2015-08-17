package org.spals.midas.io;

import java.nio.file.Path;

/**
 * Given a path, will properly locate the path in some sort of hierachy.
 *
 * @author spags
 */
public interface GoldPath {

    Path get(Path path);
}
