package org.spals.midas;

import org.testng.annotations.Test;

/**
 *
 */
public class GoldFileTest {

    // TODO expand this??
    @Test
    public void foo() {
        GoldFile.create()
            // maybe it would be awesome if we could have testng/junit determine the location
            .withClassPathReader(GoldFileTest.class, "foo")
            .withToStringSerializer()
            .run("Foo");
    }
}