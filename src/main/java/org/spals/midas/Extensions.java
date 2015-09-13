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

package org.spals.midas;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility for adding the midas extension.
 *
 * @author spags
 */
class Extensions {

    static final String MIDAS_EXT = "midas";
    static final String RESULT_EXT = "result";

    private Extensions() {
    }

    public static Path add(final Path path, final String ext) {
        final String fileName = path.getFileName().toString();
        final int extensionIndex = fileName.lastIndexOf('.');

        final Path subPath = path.getNameCount() > 1 ? path.subpath(0, path.getNameCount() - 1) : Paths.get("");

        // no extension so just append midas
        if (extensionIndex == -1) {
            return subPath.resolve(fileName + "." + ext);
        }

        final String name = fileName.substring(0, extensionIndex);
        final String extension = fileName.substring(extensionIndex + 1);

        // If midas is the last extension, then add midas.ext
        // This handles the case if you aren't allow the midas file to be overwritten.
        if (extension.equals(MIDAS_EXT)) {
            return subPath.resolve(fileName + "." + ext);
        }
        return subPath.resolve(name + "." + ext + "." + extension);
    }
}
