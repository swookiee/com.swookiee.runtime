package com.intuit.data.runtime.resource.representation;

public class BundleStatusRepresentation {

    private int state;
    private int options;

    public BundleStatusRepresentation(int state, int options) {

        this.state = state;
        this.options = options;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getOptions() {
        return options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

}
