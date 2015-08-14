package org.spals.midas;

import org.spals.midas.reader.ClasspathGoldFileReader;
import org.spals.midas.reader.FilesystemGoldFileReader;
import org.spals.midas.reader.GoldFileReader;
import org.spals.midas.serializers.ReflectionSerializer;
import org.spals.midas.serializers.Serializer;

import java.io.IOException;
import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author tkral
 */
public class GoldFileTest<T> {

    private byte[] goldFileBytes;
    private URL goldFileLocation;
    private Serializer<T> serializer;

    private GoldFileTest() {  }

    public static <T> GoldFileTest create(Class<T> goldFileType) {
        return new GoldFileTest<>();
    }

    public GoldFileTest<T> withGoldFileFromClasspath(final String goldFileLocation) throws IOException {
        this.goldFileBytes = new ClasspathGoldFileReader().readGoldFile(goldFileLocation);
        return this;
    }

    public GoldFileTest<T> withGoldFileFromFileSystem(final String goldFileLocation) throws IOException {
        this.goldFileBytes = new FilesystemGoldFileReader().readGoldFile(goldFileLocation);
        return this;
    }

    public GoldFileTest<T> withGoldFile(final String goldFileLocation, final GoldFileReader goldFileReader) throws IOException {
        this.goldFileBytes = checkNotNull(goldFileReader).readGoldFile(checkNotNull(goldFileLocation));
        return this;
    }

    public GoldFileTest<T> withDefaultSerializer() {
        this.serializer = ReflectionSerializer.builder().registerJava().build();
        return this;
    }

    public GoldFileTest<T> withCustomSerializer(final Serializer<T> serializer) {
        this.serializer = serializer;
        return this;
    }

    public void dryRun(final T object) {
    }

//    public void run(final T object) {
//
//    }

//    public void run(final T object) {
//
//    }
}
