/*******************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt, Tobias Ullrich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lars Pfannenschmidt - initial API and implementation, ongoing development and documentation
 *    Tobias Ullrich - Send correct header for basic auth
 *******************************************************************************/

package com.swookiee.runtime.ewok.auth;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swookiee.runtime.authentication.AuthenticationService;

/**
 * This HttpContext provides a simple Basic-Auth mechanism. It is using {@link AuthenticationService} to validate user
 * credentials.
 * 
 */
public class BasicAuthHttpContext implements HttpContext {

    private static final Logger logger = LoggerFactory.getLogger(BasicAuthHttpContext.class);

    private final AuthenticationService authenticationService;

    public BasicAuthHttpContext(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean handleSecurity(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        if (request.getHeader("Authorization") == null) {
            response.addHeader("WWW-Authenticate", "Basic");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        if (authenticated(request)) {
            return true;
        } else {
            response.addHeader("WWW-Authenticate", "Basic");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public URL getResource(final String name) {
        return null;
    }

    @Override
    public String getMimeType(final String name) {
        return null;
    }

    protected boolean authenticated(final HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String usernameAndPassword = new String(DatatypeConverter.parseBase64Binary(authHeader.substring(6)));
        final int userNameIndex = usernameAndPassword.indexOf(":");
        final String username = usernameAndPassword.substring(0, userNameIndex);
        final String password = usernameAndPassword.substring(userNameIndex + 1);

        if (authenticationService == null) {
            logger.warn("Could not authenticate user due to missing Authenticatiopn Service");
            return false;
        }
        return authenticationService.validateUserCredentials(username, password);
    }
}
