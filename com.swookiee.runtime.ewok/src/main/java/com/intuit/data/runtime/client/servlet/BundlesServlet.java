package com.intuit.data.runtime.client.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.data.runtime.client.servlet.util.HttpErrorException;
import com.intuit.data.runtime.client.servlet.util.ServletUtil;
import com.intuit.data.runtime.resource.representation.BundleExceptionRepresentation;

/**
 * This {@link HttpServlet} implements the Bundles Resource (5.1.2) of the OSGi RFC-182 draft version 8. @see <a href=
 * https://github.com/osgi/design/tree/master/rfcs/rfc0182> https://github.com/osgi/design/tree/master/rfcs/rfc0182</a>
 * <p>
 * <b>Sending a bundle/ jar within the body is not (yet) supported</b>
 * 
 */
public class BundlesServlet extends HttpServlet {

    public static final String ALIAS = "/framework/bundles";
    private static final long serialVersionUID = 8101122973161058308L;
    private static final Logger logger = LoggerFactory.getLogger(BundlesServlet.class);
    private final BundleContext bundleContext;
    private final ObjectMapper mapper;

    public BundlesServlet(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        ServletUtil.jsonResponse(response, getBundleUriList());

    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        try {
            String installedBundleLocation;
            if (isTextPlain(request)) {
                final String url = request.getReader().readLine();

                installedBundleLocation = installBundle(url);
                response.getWriter().println(installedBundleLocation);

            } else if (isBundleZipOrJar(request)) {

                final String location = getLocationSave(request);
                makeSureBundleIsNotInstalled(location);

                installedBundleLocation = installBundle(location, request.getInputStream());

                response.getWriter().println(installedBundleLocation);

            } else {
                throw new HttpErrorException("User not qualified Exception", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (final HttpErrorException ex) {
            ServletUtil.errorResponse(response, ex);
        }
    }

    private void makeSureBundleIsNotInstalled(final String location) throws HttpErrorException {
        if (bundleContext.getBundle(location) != null) {
            throw new HttpErrorException("Bundle with same location already installed",
                    HttpServletResponse.SC_CONFLICT);
        }
    }

    private String getLocationSave(final HttpServletRequest request) throws HttpErrorException {

        final String location = request.getHeader("Content-Location");

        // This is not exactly as described in the specification. It says SHOULD, we need a MUST since reading the
        // filename from stream (Content-Disposition name field) has no tooling since we don't have a multi part form.
        if (location == null) {
            throw new HttpErrorException("File upload must contain Content-Location header declaring location",
                    HttpServletResponse.SC_BAD_REQUEST);
        }

        return "inputstream:" + location;
    }

    private boolean isTextPlain(final HttpServletRequest request) {
        final String contentType = request.getContentType();
        return (contentType != null && contentType.equals("text/plain"));
    }

    private boolean isBundleZipOrJar(final HttpServletRequest request) {
        final String contentType = request.getContentType();
        return (contentType != null && (contentType.equals("application/vnd.osgi.bundle")
                || contentType.equals("application/zip") || contentType.equals("application/x-jar")));
    }

    private String installBundle(final String url) throws HttpErrorException {
        return installBundle(url, null);
    }

    private String installBundle(final String url, final InputStream inputStream) throws HttpErrorException {

        Bundle installedBundle;
        try {
            logger.info("Installing bundle from {}", url);
            installedBundle = bundleContext.installBundle(url, inputStream);
            return String.format("%s/%d", BundleServlet.ALIAS, installedBundle.getBundleId());
        } catch (final BundleException ex) {
            logger.error("Could not install bundle", ex);
            final String exceptionRepresentation = exceptionAsExceptionRepresentationJson(ex);
            throw new HttpErrorException("Could not install bundle", ex, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    exceptionRepresentation);
        }
    }

    private List<String> getBundleUriList() {

        final Bundle[] bundles = bundleContext.getBundles();
        final List<String> bundleList = new ArrayList<>();

        for (final Bundle bundle : bundles) {
            bundleList.add(String.format("%s/%d", BundleServlet.ALIAS, bundle.getBundleId()));
        }
        return bundleList;
    }

    private String exceptionAsExceptionRepresentationJson(final BundleException bundleException) {
        final BundleExceptionRepresentation exceptionRepresentation = new BundleExceptionRepresentation(
                bundleException.getType(), bundleException.getMessage());
        try {
            return mapper.writeValueAsString(exceptionRepresentation);
        } catch (final JsonProcessingException ex) {
            logger.error("Could not parse exception object: " + ex.getMessage(), ex);
            return "";
        }

    }

}
