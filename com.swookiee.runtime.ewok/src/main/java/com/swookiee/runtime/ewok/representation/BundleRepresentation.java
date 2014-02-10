package com.swookiee.runtime.ewok.representation;

public class BundleRepresentation {

    private long id;
    private long lastModified;
    private String location;
    private int state;
    private String symbolicName;
    private String version;

    public BundleRepresentation(long id, long lastModified, String location, int state, String symbolicName,
            String version) {
        this.id = id;
        this.lastModified = lastModified;
        this.location = location;
        this.state = state;
        this.symbolicName = symbolicName;
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public void setSymbolicName(String symbolicName) {
        this.symbolicName = symbolicName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}