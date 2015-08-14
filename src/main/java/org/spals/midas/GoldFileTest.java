package org.spals.midas;

import org.spals.midas.reader.GoldFileIO;
import org.spals.midas.reader.GoldFileIOs;
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
    private GoldFileIO reader;

    private GoldFileTest() {
    }

    public static <T> GoldFileTest create(final Class<T> goldFileType) {
        return new GoldFileTest<>();
    }

    public GoldFileTest<T> withClassPathReader(final Class<?> clazz, final String location) {
        return withReader(GoldFileIOs.classPath(clazz, location));
    }

    public GoldFileTest<T> withFileSystemReader(final String location) {
        return withReader(GoldFileIOs.fileSystem(location));
    }

    public GoldFileTest<T> withReader(final GoldFileIO reader) {
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
        final byte[] newBytes = serializer.serialize(object).getBytes(StandardCharsets.UTF_8);

        if (reader.exists()) {
            //diff
            reader.create();
        } else {
            if (Arrays.equals(reader.read(), newBytes)) {
                // awesome test passes!
            } else {

                // todo provide a differ or default differ?
            }
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
