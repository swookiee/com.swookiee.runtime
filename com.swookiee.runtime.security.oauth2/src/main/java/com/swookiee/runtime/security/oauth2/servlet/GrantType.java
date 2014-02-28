package com.swookiee.runtime.security.oauth2.servlet;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum describes the supported grant types.
 * 
 * @see http://tools.ietf.org/html/rfc6749#section-1.3
 */
public enum GrantType {

    AUTHORIZATION_CODE("authorization_code"),
    PASSWORD("password"),
    REFRESH_TOKEN("refresh_token"),
    SOCIAL_TOKEN("social_token");

    private static final Map<String, GrantType> LOOK_UP = new HashMap<>();

    static {
        for (GrantType t : GrantType.values()) {
            LOOK_UP.put(t.toString(), t);
        }
    }
    public static GrantType get(String grantType) {
        return LOOK_UP.get(grantType);
    }

    private final String type;

    private GrantType(final String type) {
        this.type = type;
    }
    @Override
    public String toString() {
        return type;
    }

}
