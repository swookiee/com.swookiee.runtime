/*******************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lars Pfannenschmidt - initial API and implementation, ongoing development and documentation
 *******************************************************************************/

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
