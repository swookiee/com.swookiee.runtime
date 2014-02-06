package com.intuit.data.runtime.client.servlet.util;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.data.runtime.resource.representation.ServiceRepresenation;

public final class ServletUtil {

    public static final String APPLICATION_JSON = "application/json";

    private static final PathIdExtractor idExtractor = new PathIdExtractor();
    private static final ObjectMapper mapper = new ObjectMapper();

    private ServletUtil() {
    }

    public static void checkForJsonMediaType(final HttpServletRequest request) throws HttpErrorException {
        if (!APPLICATION_JSON.equals(request.getContentType())) {
            throw new HttpErrorException("Unsupported Media Type", HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
    }

    /**
     * Since {@link HttpServletResponse#sendError(int, String)} could be overloaded from the default error pages the
     * specification suggests to use {@link HttpServletResponse#setStatus(int)} etc. instead to communicate an error
     * with an body.
     * 
     * @param response
     *            actual {@link HttpServletResponse}
     * @param ex
     *            current HttpErrorException
     * @throws IOException
     *             PrintWriter of {@link HttpServletResponse} could throw {@link IOException}
     */
    public static void errorResponse(final HttpServletResponse response, final HttpErrorException ex) throws IOException {
        response.setStatus(ex.getHttpErrorCode());

        if (ex.hasJsonErrorMessage()) {
            response.setContentType(APPLICATION_JSON);
            response.getWriter().println(ex.getJsonErrorMessage());
        } else {
            response.getWriter().println(ex.getMessage());
        }
    }

    public static <T> void jsonResponse(final HttpServletResponse response, final T toJson)
            throws IOException {
        final String jsonResponse = mapper.writeValueAsString(toJson);
        response.setContentType(APPLICATION_JSON);
        response.getWriter().println(jsonResponse);
    }

    public static long getId(final HttpServletRequest request) throws HttpErrorException {
        return ServletUtil.idExtractor.getId(request.getPathInfo());
    }

    public static Bundle checkAndGetBundle(final BundleContext bundleContext, final long bundleId) throws HttpErrorException {

        final Bundle bundle = bundleContext.getBundle(bundleId);

        if (bundle == null) {
            throw new HttpErrorException(String.format("Could not find Bundle %d", bundleId),
                    HttpServletResponse.SC_NOT_FOUND);
        }

        return bundle;
    }

    public static Map<String, String> transformToMapAndCleanUp(final Dictionary<String, String> source) {
        final Map<String, String> result = new HashMap<>();
        for (final Enumeration<String> keys = source.keys(); keys.hasMoreElements();) {
            final String key = keys.nextElement();
            result.put(key, source.get(key).replaceAll("[\u0000-\u001f]", ""));
        }
        return result;
    }

    public static void addServiceProperties(final ServiceReference<?> serviceReference, final ServiceRepresenation represenation) {
        for (final String propertyKey : serviceReference.getPropertyKeys()) {
            represenation.addProperty(propertyKey, serviceReference.getProperty(propertyKey));
        }
    }

    public static void addUsingBundles(final ServiceReference<?> serviceReference, final ServiceRepresenation represenation) {
        final Bundle[] usingBundles = serviceReference.getUsingBundles();

        if (usingBundles == null) {
            return;
        }

        for (final Bundle usingBundle : usingBundles) {
            represenation.addUsingBundle(usingBundle.getSymbolicName());
        }
    }
}
