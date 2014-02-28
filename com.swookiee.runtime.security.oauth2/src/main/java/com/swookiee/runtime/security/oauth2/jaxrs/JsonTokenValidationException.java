package com.swookiee.runtime.security.oauth2.jaxrs;

public class JsonTokenValidationException extends Exception {

    public JsonTokenValidationException(String message) {
        super(message);
    }

    public JsonTokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
