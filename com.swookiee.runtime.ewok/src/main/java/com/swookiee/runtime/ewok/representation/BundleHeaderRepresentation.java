package com.swookiee.runtime.ewok.representation;

import java.util.Dictionary;

public class BundleHeaderRepresentation {

    private Dictionary<String, String> headers;

    public BundleHeaderRepresentation(Dictionary<String, String> headers) {
        this.headers = headers;
    }

    public Dictionary<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Dictionary<String, String> headers) {
        this.headers = headers;
    }

}
