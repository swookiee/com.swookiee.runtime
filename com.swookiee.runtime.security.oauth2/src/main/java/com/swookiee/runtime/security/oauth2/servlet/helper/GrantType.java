package com.swookiee.runtime.security.oauth2.servlet.helper;

/**
 * This enum describes the supported grant types.
 * 
 * @see http://tools.ietf.org/html/rfc6749#section-1.3
 */
public enum GrantType {

    AUTHORIZATION_CODE("authorization_code"), PASSWORD("password"), REFRESH_TOKEN("refresh_token");

    private final String type;

    private GrantType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

}
