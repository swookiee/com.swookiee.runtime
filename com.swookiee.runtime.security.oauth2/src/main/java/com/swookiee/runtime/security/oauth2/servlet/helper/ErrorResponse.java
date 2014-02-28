package com.swookiee.runtime.security.oauth2.servlet.helper;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    private final String error;

    @SerializedName("error_description")
    private final String errorDescription;

    public ErrorResponse(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

}
