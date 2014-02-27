package com.swookiee.runtime.ewok.util;

public class HttpErrorException extends Exception {
    private static final long serialVersionUID = -1070141409536825661L;

    private final int httpErrorCode;
    private String jsonErrorMessage;

    public HttpErrorException(final String message, final int httpErrorCode) {
        super(message);
        this.httpErrorCode = httpErrorCode;
    }

    public HttpErrorException(final String message, final Throwable throwable, final int httpErrorCode) {
        super(message);
        this.httpErrorCode = httpErrorCode;
    }

    public HttpErrorException(final String message, final Throwable throwable, final int httpErrorCode, final String jsonErrorMessage) {
        super(message);
        this.httpErrorCode = httpErrorCode;
        this.jsonErrorMessage = jsonErrorMessage;
    }

    public int getHttpErrorCode() {
        return httpErrorCode;
    }

    public String getJsonErrorMessage() {
        return jsonErrorMessage;
    }

    public boolean hasJsonErrorMessage() {
        return (jsonErrorMessage != null && !jsonErrorMessage.isEmpty());
    }

}
