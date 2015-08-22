/*
 * Copyright (c) 2015, James T Spagnola & Timothy P Kral
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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