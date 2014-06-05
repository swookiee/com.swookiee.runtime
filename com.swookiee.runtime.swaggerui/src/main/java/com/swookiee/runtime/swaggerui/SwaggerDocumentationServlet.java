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

package com.swookiee.runtime.swaggerui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

import com.google.common.io.ByteStreams;

/**
 * This {@link HttpServlet} is serving the provided swagger documentation from external bundles. The injected
 * {@link Bundle} MUST contain the {@value SwaggerDocumentationTracker#SWAGGER_HEADER} header in its Manifest file. The
 * given header must point to the folder within the jar where the swagger JSON API description files are located and
 * therefore can be served. If no other path information are provided the content of the main {@code service.json} will
 * be returned.
 */
public class SwaggerDocumentationServlet extends HttpServlet {

    private static final String DEFAULT_PATH = "/service.json";
    private static final long serialVersionUID = -8476611541655924943L;

    private final Bundle bundle;

    public SwaggerDocumentationServlet(final Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
    IOException {
        final String path = getPath(request);

        final Object resourcesPath = bundle.getHeaders().get(SwaggerDocumentationTracker.SWAGGER_HEADER);
        final URL resource = bundle.getResource(resourcesPath + path);

        if (resource == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (InputStream inputStream = resource.openStream()) {
            ByteStreams.copy(inputStream, response.getOutputStream());
        }
    }

    private String getPath(final HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null || path.isEmpty() || path.equals("/")) {
            path = DEFAULT_PATH;
        }
        return path;
    }
}
