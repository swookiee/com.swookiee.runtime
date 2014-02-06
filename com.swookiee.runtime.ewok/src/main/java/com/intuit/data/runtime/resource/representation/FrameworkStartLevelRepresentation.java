package com.intuit.data.runtime.resource.representation;

public class FrameworkStartLevelRepresentation {

    private int startLevel;
    private int initialStartLevel;

    @SuppressWarnings("unused")
    private FrameworkStartLevelRepresentation() {
    }

    public FrameworkStartLevelRepresentation(final int startLevel, final int initialStartLevel) {
        this.startLevel = startLevel;
        this.initialStartLevel = initialStartLevel;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(final int startLevel) {
        this.startLevel = startLevel;
    }

    public int getInitialStartLevel() {
        return initialStartLevel;
    }
}
