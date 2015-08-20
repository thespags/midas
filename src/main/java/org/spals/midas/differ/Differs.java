package org.spals.midas.differ;

/**
 * Factory of built in differs.
 *
 * @author spags
 */
public final class Differs {

    private Differs() {
    }

    public static Differ strings() {
        return new StringDiffer();
    }
}
