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

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionHamcrestMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.spals.midas.differ.Differ;
import net.spals.midas.io.FileUtil;

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
    private GoldFile gold;

    @BeforeMethod
    public void setUp(final Method method) {
        MockitoAnnotations.initMocks(this);

        gold = GoldFile.<String>builder()
            .withFileUtil(files)
            .build();
        methodPath = Paths.get(method.getName());
    }

    @Test
    public void testBadDiff() {
        when(files.readAllBytes(any(Path.class))).thenReturn("Barfoo".getBytes());
        catchException(() -> gold.run("Foobar", methodPath));
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
        catchException(() -> gold.run("Foobar", methodPath));
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
        catchException(() -> gold.run("Foobar\nExtraNew", methodPath));
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
        catchException(() -> gold.run("bar", methodPath));

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
