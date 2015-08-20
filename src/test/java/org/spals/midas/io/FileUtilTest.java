package org.spals.midas.io;

import org.spals.midas.GoldFileException;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class FileUtilTest {

    private static final Path FOO = Paths.get("foo");
    private static final Path BAD_PATH = Paths.get("/a//a");
    private static final FileUtil files = new FileUtil();

    @Test
    public void testWriteReadAllBytes() throws Exception {
        final byte[] bytes = "Foo".getBytes();
        files.write(FOO, bytes);
        assertThat(files.readAllBytes(FOO), is(bytes));

        files.delete(FOO);
        assertThat(files.exists(FOO), is(false));
    }

    @Test
    public void testBadReadAllBytes() throws Exception {
        catchException(files).readAllBytes(BAD_PATH);
        assertThat(caughtException(), instanceOf(GoldFileException.class));
    }

    @Test
    public void testBadWrite() throws Exception {
        final byte[] bytes = "Foo".getBytes();
        catchException(files).write(BAD_PATH, bytes);
        assertThat(caughtException(), instanceOf(GoldFileException.class));
    }

    @Test
    public void testMakeDirs() throws Exception {
        files.makeDirs(FOO.toFile());
        files.delete(FOO);
        assertThat(files.exists(FOO), is(false));
    }

    @Test
    public void testBadMakeDirs() throws Exception {
        catchException(files).makeDirs(BAD_PATH.toFile());
        assertThat(caughtException(), instanceOf(GoldFileException.class));
    }

    @Test
    public void testMakeParents() throws Exception {
        files.makeParents(Paths.get("Bar", "Foo"));
        files.delete(Paths.get("Bar"));
        assertThat(files.exists(FOO), is(false));
    }

    @Test
    public void testCreateFile() throws Exception {
        files.createFile(FOO);
        assertThat(files.exists(FOO), is(true));
        files.delete(FOO);
        assertThat(files.exists(FOO), is(false));
    }

    @Test
    public void testBadCreateFile() throws Exception {
        catchException(files).createFile(BAD_PATH);
        assertThat(caughtException(), instanceOf(GoldFileException.class));
    }

    @Test
    public void testBadDelete() throws Exception {
        catchException(files).delete(FOO);
        assertThat(caughtException(), instanceOf(GoldFileException.class));
    }
}