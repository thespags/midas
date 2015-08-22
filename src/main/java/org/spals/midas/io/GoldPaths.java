/*
 * Copyright (c) 2015, James T Spagnola & Timothy P Kral
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

package org.spals.midas.io;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A bunch of {@link GoldPath}'s that help us locate files into some sort of directory hierarchy.
 * Commonly using the maven src/test/resources to direct the gold files into some sort of package associated
 * to the tests package.
 *
 * @author spags
 */
public final class GoldPaths {

    public static final Path MAVEN = Paths.get("src", "test", "resources");

    private GoldPaths() {
    }

    /**
     * Locates the path into parent/full/class/name/file
     */
    public static GoldPath fullClass(final Path parent, final Class<?> clazz) {
        return new ParentPath(parent.resolve(clazz.getName().replace(".", File.separator)));
    }

    /**
     * Locates the path into parent/classname/file
     */
    public static GoldPath simpleClass(final Path parent, final Class<?> clazz) {
        return new ParentPath(parent.resolve(clazz.getSimpleName()));
    }

    /**
     * Locates the path into parent/file
     */
    public static GoldPath parent(final Path parent) {
        return new ParentPath(parent);
    }

    /**
     * Locates the path into file
     */
    public static GoldPath simple() {
        return new SimplePath();
    }

    private static class ParentPath implements GoldPath {

        private final Path parent;

        public ParentPath(final Path parent) {
            this.parent = parent;
        }

        @Override
        public Path get(final Path path) {
            return parent.resolve(path);
        }
    }

    private static class SimplePath implements GoldPath {
        @Override
        public Path get(final Path path) {
            return path;
        }
    }
}
