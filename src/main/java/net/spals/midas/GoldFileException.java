package net.spals.midas;

/**
 * An error that occurred while running a gold file test.
 *
 * @author spags
 */
public class GoldFileException extends RuntimeException {
    public GoldFileException(final String message) {
        super(message);
    }

    public GoldFileException(final Throwable cause) {
        super(cause);
    }
}
