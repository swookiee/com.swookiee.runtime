package com.swookiee.runtime.ewok.representation;

public class BundleStatusRepresentation {

    private int state;
    private int options;

    public BundleStatusRepresentation() {
    }

    public BundleStatusRepresentation(final int state, final int options) {
        this.state = state;
        this.options = options;
    }

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public int getOptions() {
        return options;
    }

    public void setOptions(final int options) {
        this.options = options;
    }

}
