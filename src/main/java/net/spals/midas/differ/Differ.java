package net.spals.midas.differ;

/**
 * Provides an algorithm for diffing the results of a gold file run.
 *
 * @author spags
 */
public interface Differ {

    String diff(final byte[] oldBytes, final byte[] newBytes);
}
