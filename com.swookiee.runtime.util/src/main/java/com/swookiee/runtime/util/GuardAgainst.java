package com.swookiee.runtime.util;

public final class GuardAgainst {

    private GuardAgainst() {
    }

    public static <T> T nullValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static <T> T nullValue(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
