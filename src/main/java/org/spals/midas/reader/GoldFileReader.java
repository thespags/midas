package org.spals.midas.reader;

import java.io.IOException;

/**
 * @author tkral
 */
public interface GoldFileReader {

    byte[] readGoldFile(String goldFileLocation) throws IOException;
}
