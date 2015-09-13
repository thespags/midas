package net.spals.midas.differ;

import net.spals.midas.util.Tests;
import org.testng.annotations.Test;

/**
 * @author spags
 */
public class DiffersTest {

    @Test
    public void testPrivate() throws Exception {
        Tests.testPrivate(Differs.class);
    }
}