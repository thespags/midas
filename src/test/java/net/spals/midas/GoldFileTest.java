package net.spals.midas;

import net.spals.midas.io.GoldPaths;
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

    public static final GoldFile<Object> SIMPLE_CLASS_GOLD = GoldFile.builder()
        .withPath(GoldPaths.simpleClass(GoldPaths.MAVEN, GoldFileTest.class))
        .build();
    private Path methodPath;

    @BeforeMethod
    public void setUp(final Method method) {
        this.methodPath = Paths.get(method.getName());
    }

    @Test
    public void testFullClass() {
        GoldFile.builder()
            .withPath(GoldPaths.fullClass(GoldPaths.MAVEN, GoldFileTest.class))
            .build()
            .run("Foo", methodPath);
    }

    @Test
    public void testSimpleClass() {
        SIMPLE_CLASS_GOLD.run("Foobar", methodPath);
    }

    @Test
    public void testTimeStamp() throws URISyntaxException {
        // get the original time stamp.
        final Path path = GoldPaths.simpleClass(GoldPaths.MAVEN, GoldFileTest.class)
            .get(Paths.get(methodPath + ".midas"));

        assertThat(path.toFile().exists(), is(true));
        final long oldTimeStamp = path.toFile().lastModified();

        // update the time stamp
        SIMPLE_CLASS_GOLD.run("Foobar", methodPath);

        final long newTimeStamp = path.toFile().lastModified();
        assertThat(newTimeStamp, greaterThan(oldTimeStamp));
    }

    @Test
    public void testReflection() {
        GoldFile.builder()
            .withPath(GoldPaths.simpleClass(GoldPaths.MAVEN, GoldFileTest.class))
            .withReflectionSerializer()
            .build()
            .run(new Foo(), methodPath);
    }

    @SuppressWarnings("unused")
    private static class Foo {

        private final String string = "foo";
    }
}