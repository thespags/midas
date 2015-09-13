package net.spals.midas.io;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A bunch of {@link GoldPath}'s that help us locate files into some sort of directory hierarchy.
 * Commonly using the maven src/test/resources to direct the gold files into some sort of package associated
 * to the tests package.
 *
 * @author spags
 */
public final class GoldPaths {

    public static final Path MAVEN = Paths.get("src", "test", "resources");

    private GoldPaths() {
    }

    /**
     * Locates the path into parent/full/class/name/file
     */
    public static GoldPath fullClass(final Path parent, final Class<?> clazz) {
        return new ParentPath(parent.resolve(clazz.getName().replace(".", File.separator)));
    }

    /**
     * Locates the path into parent/classname/file
     */
    public static GoldPath simpleClass(final Path parent, final Class<?> clazz) {
        return new ParentPath(parent.resolve(clazz.getSimpleName()));
    }

    /**
     * Locates the path into parent/file
     */
    public static GoldPath parent(final Path parent) {
        return new ParentPath(parent);
    }

    /**
     * Locates the path into file
     */
    public static GoldPath simple() {
        return new SimplePath();
    }

    private static class ParentPath implements GoldPath {

        private final Path parent;

        public ParentPath(final Path parent) {
            this.parent = parent;
        }

        @Override
        public Path get(final Path path) {
            return parent.resolve(path);
        }
    }

    private static class SimplePath implements GoldPath {
        @Override
        public Path get(final Path path) {
            return path;
        }
    }
}
