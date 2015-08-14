package org.spals.midas;

import org.spals.midas.reader.ClasspathGoldFileReader;
import org.spals.midas.reader.FilesystemGoldFileReader;
import org.spals.midas.reader.GoldFileReader;
import org.spals.midas.reader.GoldFileReaders;
import org.spals.midas.serializers.ReflectionSerializer;
import org.spals.midas.serializers.Serializer;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author tkral
 */
public class GoldFileTest<T> {

    private byte[] goldFileBytes;
    private URL goldFileLocation;
    private Serializer<T> serializer;
    private GoldFileReader reader;

    private GoldFileTest() {
    }

    public static <T> GoldFileTest create(final Class<T> goldFileType) {
        return new GoldFileTest<>();
    }

    public GoldFileTest<T> withClassPathReader() {
        return withReader(GoldFileReaders.CLASS_PATH);
    }

    public GoldFileTest<T> withFileSystemReader() {
        return withReader(GoldFileReaders.FILE_SYSTEM);
    }

    public GoldFileTest<T> withReader(final GoldFileReader reader) {
        this.reader = reader;
        return this;
    }

    public GoldFileTest<T> withDefaultSerializer() {
        this.serializer = ReflectionSerializer.builder().registerJava().build();
        return this;
    }

    public GoldFileTest<T> withSerializer(final Serializer<T> serializer) {
        this.serializer = serializer;
        return this;
    }

    public void dryRun(final T object) {
        byte[] newBytes = serializer.serialize(object).getBytes(StandardCharsets.UTF_8);

        if (reader.exists(goldFileLocation)) {
            //diff
            reader.create();
        } else {
            Arrays.equals(reader.read(), newBytes);
        }


        reader.write(newBytes);

    }

//    public void run(final T object) {
//
//    }

//    public void run(final T object) {
//
//    }
}
