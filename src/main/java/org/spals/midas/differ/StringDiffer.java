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

package org.spals.midas.differ;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Uses {@link DiffUtils} to properly determine the differences between the old and new data.
 *
 * @author spags
 */
class StringDiffer implements Differ {

    public String diff(final byte[] oldBytes, final byte[] newBytes) {
        final String[] oldStrings = new String(oldBytes, StandardCharsets.UTF_8).split("\n");
        final String[] newStrings = new String(newBytes, StandardCharsets.UTF_8).split("\n");

        final Patch<String> patch = DiffUtils.diff(Arrays.asList(oldStrings), Arrays.asList(newStrings));

        final StringBuilder builder = new StringBuilder();

        for (final Delta<String> delta : patch.getDeltas()) {
            switch (delta.getType()) {
                case CHANGE:
                    chunk(builder, delta.getOriginal(), " << ");
                    chunk(builder, delta.getRevised(), " >> ");
                    break;
                case DELETE:
                    chunk(builder, delta.getOriginal(), " << ");
                    break;
                case INSERT:
                    chunk(builder, delta.getRevised(), " >> ");
                    break;
            }
        }
        return builder.toString();
    }

    private void chunk(final StringBuilder builder, final Chunk<String> chunk, final String dir) {
        builder.append(String.format("%s", chunk.getPosition()))
            .append(dir)
            .append(StreamSupport.stream(chunk.getLines().spliterator(), false).collect(Collectors.joining("\n")))
            .append("\n");
    }
}
