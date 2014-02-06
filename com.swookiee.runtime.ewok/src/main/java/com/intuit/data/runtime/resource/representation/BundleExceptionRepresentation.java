package com.intuit.data.runtime.resource.representation;

public class BundleExceptionRepresentation {

    private int typecode;
    private String message;

    public BundleExceptionRepresentation() {
    }

    public BundleExceptionRepresentation(int typecode, String message) {
        this.typecode = typecode;
        this.message = message;
    }

    public int getTypecode() {
        return typecode;
    }

    public void setTypecode(int typecode) {
        this.typecode = typecode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
