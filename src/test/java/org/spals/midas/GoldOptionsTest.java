package org.spals.midas;


import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.spals.midas.io.FileUtil;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author spags
 */
public class GoldOptionsTest {

    public static final byte[] BYTES = "foo".getBytes();
    @Mock
    private FileUtil files;
    private GoldFile<String> gold;

    @BeforeMethod
    public void setUp(final Method method) {
        MockitoAnnotations.initMocks(this);

        this.gold = GoldFile.<String>builder()
            .withFileUtil(files)
            .build();
    }

    @Test
    public void testNotWritable() {
        when(files.readAllBytes(any(Path.class))).thenReturn(BYTES);
        gold.run("foo", Paths.get("ignored"), GoldOptions.create().setWritable(false));

        // write should never have occurred
        verify(files, times(0)).write(any(Path.class), any(byte[].class));
    }

    @Test
    public void testNotCheckout() {
        when(files.readAllBytes(any(Path.class))).thenReturn(BYTES);
        gold.run("foo", Paths.get("ignored"), GoldOptions.create().setCheckout(false));

        // write should be to result file
        verify(files, times(1)).write(Paths.get("ignored.midas.result"), BYTES);
    }
}