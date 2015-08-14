package org.spals.midas.reader;

import java.io.IOException;

/**
 * @author tkral
 */
public interface GoldFileReader {

    byte[] read() throws IOException;

    boolean exists(String location);
}
