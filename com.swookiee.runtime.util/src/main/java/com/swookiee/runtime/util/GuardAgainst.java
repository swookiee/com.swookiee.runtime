package com.swookiee.runtime.util;

public final class GuardAgainst {

    private GuardAgainst() {
    }

    public static void nullValue(final Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Null value is not allowed");
        }
    }

    public static void nullValue(final Object... values) {
        for (final Object value : values) {
            GuardAgainst.nullValue(value);
        }
    }
}
