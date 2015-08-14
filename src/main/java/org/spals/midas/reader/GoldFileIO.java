package org.spals.midas.reader;

import java.io.IOException;

/**
 * @author tkral
 */
public interface GoldFileIO {

    byte[] read();

    boolean exists();

    void create();

    void write(byte[] newBytes);
}
