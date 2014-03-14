package com.swookiee.runtime.ewok.representation;

import java.util.Dictionary;

public class BundleHeaderRepresentation {

    private Dictionary<String, String> headers;

    public BundleHeaderRepresentation() {
    }

    public BundleHeaderRepresentation(final Dictionary<String, String> headers) {
        this.headers = headers;
    }

    public Dictionary<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(final Dictionary<String, String> headers) {
        this.headers = headers;
    }

}
