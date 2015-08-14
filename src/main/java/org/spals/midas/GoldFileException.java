package org.spals.midas;

public class GoldFileException extends RuntimeException {
    public GoldFileException(final String message) {
        super(message);
    }

    public GoldFileException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
