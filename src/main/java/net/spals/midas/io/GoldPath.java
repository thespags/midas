package net.spals.midas.io;

import java.nio.file.Path;

/**
 * Given a path, will properly locate the path in some sort of hierarchy.
 *
 * @author spags
 */
public interface GoldPath {

    Path get(Path path);
}
