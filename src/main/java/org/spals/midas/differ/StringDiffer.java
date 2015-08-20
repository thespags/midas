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
