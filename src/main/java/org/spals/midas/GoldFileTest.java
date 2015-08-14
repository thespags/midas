package org.spals.midas;

import org.spals.midas.reader.GoldFileReader;
import org.spals.midas.reader.GoldFileReaders;
import org.spals.midas.serializers.ReflectionSerializer;
import org.spals.midas.serializers.Serializer;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

    public GoldFileTest<T> withClassPathReader(final String location) {
        return withReader(GoldFileReaders.classPathReader(location));
    }

    public GoldFileTest<T> withFileSystemReader(final String location) {
        return withReader(GoldFileReaders.fileSystemReader(location));
    }

    public GoldFileTest<T> withReader(final GoldFileReader reader) {
        this.reader = reader;
        return this;
    }

    public GoldFileTest<T> withDefaultSerializer() {
        return withSerializer(ReflectionSerializer.builder().registerJava().build());
    }

    public GoldFileTest<T> withSerializer(final Serializer<T> serializer) {
        this.serializer = serializer;
        return this;
    }

    public void dryRun(final T object) {
        byte[] newBytes = serializer.serialize(object).getBytes(StandardCharsets.UTF_8);

        if (reader.exists()) {
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
