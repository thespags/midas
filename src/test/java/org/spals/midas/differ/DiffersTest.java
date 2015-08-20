package org.spals.midas.differ;

import org.spals.midas.util.Tests;
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