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

        final Object resourcesPath = bundle.getHeaders().get("X-Swagger-Documentation");
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
