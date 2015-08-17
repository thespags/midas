package org.spals.midas.io;

import org.spals.midas.util.Tests;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GoldPathsTest {

    private static final Path FOO = Paths.get("foo");

    @Test
    public void testFullClass() throws IOException {
        final GoldPath path = GoldPaths.fullClass(GoldPaths.MAVEN, this.getClass());
        assertThat(path.get(FOO).toString(), is("src/test/resources/org/spals/midas/io/GoldPathsTest/foo"));
    }

    @Test
    public void testSimpleClass() {
        final GoldPath path = GoldPaths.simpleClass(GoldPaths.MAVEN, this.getClass());
        assertThat(path.get(FOO).toString(), is("src/test/resources/GoldPathsTest/foo"));
    }

    @Test
    public void testParent() {
        final GoldPath path = GoldPaths.parent(Paths.get("foo", "bar"));
        assertThat(path.get(FOO).toString(), is("foo/bar/foo"));
    }

    @Test
    public void testSimple() {
        final GoldPath path = GoldPaths.simple();
        assertThat(path.get(FOO).toString(), is("foo"));
    }

    @Test
    public void testPrivate() throws Exception {
        Tests.testPrivate(GoldPaths.class);
    }
}