package com.swookiee.runtime.security.oauth2.token;

public class AuthenticationException extends Exception {

    public enum AuthenticationError {

        AUTH_CODE_DIFFERENT_CLIENT_ID("The auth code '%s' created for client '%s' can not be used with client '%s'."),
        AUTH_CODE_EXPIRED("Auth code '%s' expired."),
        AUTH_CODE_NOT_FOUND("Auth code '%s' not found."),
        CLIENT_NOT_FOUND("Client with ID '%s' not found."),

        REFRESH_TOKEN__DIFFERENT_CLIENT_ID("The refresh token '%s' created for client '%s' can not be used with client '%s'."),
        REFRESH_TOKEN_EXPIRED("Refresh token '%s' expired."),
        REFRESH_TOKEN_NOT_FOUND("Refresh token '%s' not found.");

        private String message;

        private AuthenticationError(String message) {
            this.message = message;
        }

        public String getMessage(Object... args) {
            return String.format(message, args);
        }
    }

    private final AuthenticationError authenticationError;
    private final String message;

    public AuthenticationException(AuthenticationError authenticationError, Object... args) {
        this.authenticationError = authenticationError;
        this.message = authenticationError.getMessage(args);
    }

    public AuthenticationError getAuthenticationError() {
        return authenticationError;
    }

    @Override
    public String getMessage() {
        return authenticationError.name() + ": " + message;
    }

}
