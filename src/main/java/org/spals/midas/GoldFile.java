package org.spals.midas;

import com.google.common.base.Preconditions;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.spals.midas.reader.GoldFileIO;
import org.spals.midas.reader.GoldFileIOs;
import org.spals.midas.serializers.ReflectionSerializer;
import org.spals.midas.serializers.Serializer;
import org.spals.midas.serializers.Serializers;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author tkral
 */
public class GoldFile<T> {

    private Serializer<T> serializer;
    private GoldFileIO goldFileIO;

    private GoldFile() {
    }

    public static <T> GoldFile<T> create(final Class<T> goldFileType) {
        Preconditions.checkNotNull(goldFileType);
        return new GoldFile<>();
    }

    public static GoldFile<Object> create() {
        return new GoldFile<>();
    }

    public GoldFile<T> withClassPathReader(final Class<?> clazz, final String location) {
        Preconditions.checkNotNull(location);
        return withReader(GoldFileIOs.classPath(clazz, location));
    }

    public GoldFile<T> withFileSystemReader(final String location) {
        Preconditions.checkNotNull(location);
        return withReader(GoldFileIOs.fileSystem(location));
    }

    public GoldFile<T> withReader(final GoldFileIO reader) {
        this.goldFileIO = reader;
        return this;
    }

    public GoldFile<T> withReflectionSerializer() {
        return withSerializer(ReflectionSerializer.builder().registerJava().build());
    }

    public GoldFile<T> withToStringSerializer() {
        return withSerializer(Serializers.of());
    }

    public GoldFile<T> withSerializer(final Serializer<T> serializer) {
        this.serializer = serializer;
        return this;
    }

    public void dryRun(final T object) {
        final String newString = serializer.serialize(object);
        final byte[] newBytes = newString.getBytes(StandardCharsets.UTF_8);

        if (!goldFileIO.exists()) {
            // dry run will throw an error if it couldn't actually perform a run
            throw new GoldFileException("file does not exist");
        } else {
            final byte[] oldBytes = goldFileIO.read();
            if (!Arrays.equals(oldBytes, newBytes)) {
                diff(newString, oldBytes);
            }
        }
        // dry run shouldn't write
    }

    public void run(final T object) {
        final String newString = serializer.serialize(object);
        final byte[] newBytes = newString.getBytes(StandardCharsets.UTF_8);

        if (!goldFileIO.exists()) {
            goldFileIO.create();
        } else {
            final byte[] oldBytes = goldFileIO.read();
            if (!Arrays.equals(oldBytes, newBytes)) {
                System.err.println(diff(newString, oldBytes));
            } else {
                return;
            }
        }

        goldFileIO.write(newBytes);
        throw new GoldFileException("todome");
    }

    private String diff(final String newString, final byte[] oldBytes) {
        final String[] newStrings = newString.split("\n");
        final String[] oldStrings = new String(oldBytes, StandardCharsets.UTF_8).split("\n");
        final Patch<String> patch = DiffUtils.diff(Arrays.asList(oldStrings), Arrays.asList(newStrings));

        final StringBuilder builder = new StringBuilder();

        for (final Delta<String> delta : patch.getDeltas()) {
            switch (delta.getType()) {
                case CHANGE:
                    builder.append(" << ").append(join(delta.getOriginal())).append("\n")
                        .append(" >> ").append(join(delta.getRevised())).append("\n");
                    break;
                case DELETE:
                    builder.append(" << ").append(join(delta.getOriginal())).append("\n");
                    break;
                case INSERT:
                    builder.append(" >> ").append(join(delta.getRevised())).append("\n");
                    break;
            }
        }
        return builder.toString();
    }

    private String join(final Chunk<String> chunk) {
        return StreamSupport.stream(chunk.getLines().spliterator(), false)
            .collect(Collectors.joining("\n"));
    }
}
