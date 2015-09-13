package net.spals.midas;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.spals.midas.differ.Differ;
import net.spals.midas.io.FileUtil;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionHamcrestMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests for negative outcomes of {@link GoldFile}
 *
 * @author spags
 */
public class BadGoldFileTest {

    @Mock
    private Differ differ;
    @Mock
    private FileUtil files;
    private Path methodPath;
    private GoldFile<String> gold;

    @BeforeMethod
    public void setUp(final Method method) {
        MockitoAnnotations.initMocks(this);

        this.gold = GoldFile.<String>builder()
            .withFileUtil(files)
            .build();
        this.methodPath = Paths.get(method.getName());
    }

    @Test
    public void testBadDiff() {
        when(files.readAllBytes(any(Path.class))).thenReturn("Barfoo".getBytes());
        catchException(gold).run("Foobar", methodPath);
        assertThat(
            caughtException(),
            allOf(
                instanceOf(GoldFileException.class),
                hasMessage("\nDiffs: testBadDiff.midas\n0 << Barfoo\n0 >> Foobar\n")
            )
        );
    }

    @Test
    public void testDeleteDiff() {
        when(files.readAllBytes(any(Path.class))).thenReturn("Foobar\nExtraOld".getBytes());
        catchException(gold).run("Foobar", methodPath);
        assertThat(
            caughtException(),
            allOf(
                instanceOf(GoldFileException.class),
                hasMessage("\nDiffs: testDeleteDiff.midas\n1 << ExtraOld\n")
            )
        );
    }

    @Test
    public void testInsertDiff() {
        when(files.readAllBytes(any(Path.class))).thenReturn("Foobar".getBytes());
        catchException(gold).run("Foobar\nExtraNew", methodPath);
        assertThat(
            caughtException(),
            allOf(
                instanceOf(GoldFileException.class),
                hasMessage("\nDiffs: testInsertDiff.midas\n1 >> ExtraNew\n")
            )
        );
    }

    @Test
    public void testRandomDiff() {
        gold = GoldFile.<String>builder()
            .withFileUtil(files)
            .withDiffer(differ)
            .build();

        when(differ.diff(any(byte[].class), any(byte[].class))).thenReturn("Hello world!");
        when(files.readAllBytes(any(Path.class))).thenReturn("foo".getBytes());
        catchException(gold).run("bar", methodPath);

        assertThat(
            caughtException(),
            allOf(
                instanceOf(GoldFileException.class),
                hasMessage("\nDiffs: testRandomDiff.midas\nHello world!")
            )
        );
    }

    @Test
    public void testBadTimestamp() {
        when(files.readAllBytes(any(Path.class))).thenReturn("Foobar".getBytes());
        gold.run("Foobar", methodPath);
    }
}
