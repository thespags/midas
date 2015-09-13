package net.spals.midas;

/**
 * A bunch of options for changing the behavior of a gold file run.
 *
 * @author spgas
 */
public class GoldOptions {

    private boolean writable;
    private boolean checkout;

    private GoldOptions() {
        this.writable = true;
        this.checkout = true;
    }

    public static GoldOptions create() {
        return new GoldOptions();
    }

    /**
     * Can we overwrite the current file or do we write a back up?
     */
    public boolean checkout() {
        return checkout;
    }

    public GoldOptions setCheckout(final boolean checkout) {
        this.checkout = checkout;
        return this;
    }

    /**
     * Should we actually write a file to the system?
     */
    public boolean writable() {
        return writable;
    }

    public GoldOptions setWritable(final boolean writable) {
        this.writable = writable;
        return this;
    }
}
