package com.swookiee.runtime.security.oauth2.servlet;
public enum OAuthErrorCode {

    ACCESS_DENIED("access_denied"), INVALID_CLIENT("invalid_client"), INVALID_GRANT("invalid_grant"), INVALID_REQUEST("invalid_request"), INVALID_SCOPE("invalid_scope"),
    SERVER_ERROR("server_error"),
    UNAUTHORIZED_CLIENT("unauthorized_client"),
    UNSUPPORTED_GRANT_TYPE("unsupported_grant_type");

    private String error;

    private OAuthErrorCode(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

}