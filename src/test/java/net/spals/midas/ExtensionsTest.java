package net.spals.midas;

import net.spals.midas.util.Tests;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author spags
 */
public class ExtensionsTest {

    @Test
    public void testPrivate() throws Exception {
        Tests.testPrivate(Extensions.class);
    }

    @Test
    public void testAddNoExtension() {
        final Path path = Paths.get("foo");
        assertThat(Extensions.add(path, "ext").toString(), is("foo.ext"));
    }

    @Test
    public void testAddComplexNoExtension() {
        final Path path = Paths.get("foo", "bar");
        assertThat(Extensions.add(path, "ext").toString(), is("foo/bar.ext"));
    }

    @Test
    public void testAddExtension() {
        final Path path = Paths.get("foo.txt");
        assertThat(Extensions.add(path, "ext").toString(), is("foo.ext.txt"));
    }

    @Test
    public void testAddComplexExtension() {
        final Path path = Paths.get("foo", "bar.txt");
        assertThat(Extensions.add(path, "ext").toString(), is("foo/bar.ext.txt"));
    }
}