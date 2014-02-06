package com.intuit.data.runtime.client.servlet.util;

public class HttpErrorException extends Exception {

    private static final long serialVersionUID = -1070141409536825661L;

    private final int httpErrorCode;
    private String jsonErrorMessage;

    public HttpErrorException(String message, int httpErrorCode) {
        super(message);
        this.httpErrorCode = httpErrorCode;
    }

    public HttpErrorException(String message, Throwable throwable, int httpErrorCode) {
        super(message);
        this.httpErrorCode = httpErrorCode;
    }

    public HttpErrorException(String message, Throwable throwable, int httpErrorCode, String jsonErrorMessage) {
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
