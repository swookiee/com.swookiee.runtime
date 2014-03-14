package com.swookiee.runtime.security.oauth2.token;

public class TokenCreationException extends Exception {

    public TokenCreationException(String message, Exception cause) {
        super(message, cause);
    }
}
