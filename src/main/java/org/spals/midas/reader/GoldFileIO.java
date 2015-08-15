package org.spals.midas.reader;

/**
 * @author tkral
 */
public interface GoldFileIO {

    byte[] read();

    boolean exists();

    void create();

    void write(byte[] newBytes);
}
