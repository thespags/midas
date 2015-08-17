package org.spals.midas.util;

import org.testng.annotations.Test;

/**
 * @author spags
 */
public class PreconditionsTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCheckArgument() throws Exception {
        Preconditions.checkArgument(false, "foo");
    }

    @Test
    public void testPrivate() throws Exception {
        Tests.testPrivate(Preconditions.class);
    }
}