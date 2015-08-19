package org.spals.midas;

import org.spals.midas.io.GoldPaths;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * Tests for positive outcomes and the overall flow of {@link GoldFile}
 *
 * @author spags
 */
public class GoldFileTest {

    private Method method;

    @BeforeMethod
    public void setUp(final Method method) {
        this.method = method;
    }

    @Test
    public void testFullClass() {
        GoldFile.builder()
            .withPath(GoldPaths.fullClass(GoldPaths.MAVEN, GoldFileTest.class))
            .build()
            .run("Foo", method.getName());
    }

    @Test
    public void testSimpleClass() {
        GoldFile.builder()
            .withPath(GoldPaths.simpleClass(GoldPaths.MAVEN, GoldFileTest.class))
            .build()
            .run("Foobar", method.getName());
    }

    @Test
    public void testTimeStamp() throws URISyntaxException {
        // get the original time stamp.
        final Path path = GoldPaths.simpleClass(GoldPaths.MAVEN, GoldFileTest.class)
            .get(Paths.get(method.getName() + ".midas"));

        assertThat(path.toFile().exists(), is(true));
        final long oldTimeStamp = path.toFile().lastModified();

        // update the time stamp
        GoldFile.builder()
            .withPath(GoldPaths.simpleClass(GoldPaths.MAVEN, GoldFileTest.class))
            .build()
            .run("Foobar", method.getName());

        final long newTimeStamp = path.toFile().lastModified();
        assertThat(newTimeStamp, greaterThan(oldTimeStamp));
    }
}