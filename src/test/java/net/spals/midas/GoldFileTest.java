/*
 * Copyright (c) 2016, James T Spagnola & Timothy P Kral
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.spals.midas;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.spals.midas.io.GoldPaths;

/**
 * Tests for positive outcomes and the overall flow of {@link GoldFile}
 *
 * @author spags
 */
public class GoldFileTest {

    private static final GoldFile SIMPLE_CLASS_GOLD = GoldFile.builder()
        .withPath(GoldPaths.simpleClass(GoldPaths.MAVEN, GoldFileTest.class))
        .build();
    private Path methodPath;

    @BeforeMethod
    public void setUp(final Method method) {
        methodPath = Paths.get(method.getName());
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
            .build()
            .run(new Foo(), methodPath);
    }

    @SuppressWarnings("unused")
    private static class Foo {

        private final String string = "foo";
    }
}