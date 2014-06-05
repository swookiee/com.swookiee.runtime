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

package com.swookiee.runtime.ewok.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swookiee.runtime.ewok.representation.BundleRepresentation;
import com.swookiee.runtime.ewok.representation.BundleStartlevelRepresentation;
import com.swookiee.runtime.ewok.representation.BundleStatusRepresentation;
import com.swookiee.runtime.ewok.util.HttpErrorException;
import com.swookiee.runtime.ewok.util.ServletUtil;

/**
 * This {@link HttpServlet} implements the Bundle Resource (5.1.3), Bundle State Resource (5.1.4), Bundle Header
 * Resource (5.1.5) & Bundle Startlevel Resource (5.1.6) of the OSGi RFC-182 draft version 8. @see <a href=
 * https://github.com/osgi/design/tree/master/rfcs/rfc0182> https://github.com/osgi/design/tree/master/rfcs/rfc0182</a>
 * 
 */
public class BundleServlet extends HttpServlet {

    public static final String ALIAS = "/framework/bundle";
    private static final long serialVersionUID = -8784847583201231060L;
    private static final Logger logger = LoggerFactory.getLogger(BundleServlet.class);
    private final BundleContext bundleContext;
    private final ObjectMapper mapper;

    public BundleServlet(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {

            final long bundleId = ServletUtil.getId(request);
            final Bundle bundle = ServletUtil.checkAndGetBundle(bundleContext, bundleId);
            String json;

            if (isStartLevelRequest(request)) {
                json = getBundleStartlevel(bundle);
            } else if (isHeaderRequest(request)) {
                final Map<String, String> header = ServletUtil.transformToMapAndCleanUp(bundle.getHeaders());
                json = mapper.writeValueAsString(header);
            } else if (isStateRequest(request)) {
                final BundleStatusRepresentation bundleStatusRepresentation = new BundleStatusRepresentation(
                        bundle.getState(), 0);
                json = mapper.writeValueAsString(bundleStatusRepresentation);
            } else {
                json = getBundle(bundle);
            }
            response.setContentType(ServletUtil.APPLICATION_JSON);
            response.getWriter().println(json);
        } catch (final HttpErrorException ex) {
            logger.info(ex.getMessage());
            response.sendError(ex.getHttpErrorCode());
        }
    }

    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            final long bundleId = ServletUtil.getId(request);
            final Bundle bundle = ServletUtil.checkAndGetBundle(bundleContext, bundleId);

            if (isStartLevelRequest(request)) {
                updateBundleStartlevel(request, bundle);
            } else if (isStateRequest(request)) {
                updateBundleStatus(request, bundle);
            } else {
                updateBundle(request, bundle);
            }
        } catch (final HttpErrorException ex) {
            logger.error(ex.getMessage(), ex);
            response.sendError(ex.getHttpErrorCode());
        } catch (final BundleException | IOException ex) {
            logger.error("Could not update Bundle", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isStartLevelRequest(final HttpServletRequest request) {
        return request.getPathInfo().matches("/[0-9]+/startlevel");
    }

    private boolean isHeaderRequest(final HttpServletRequest request) {
        return request.getPathInfo().matches("/[0-9]+/header");
    }

    private boolean isStateRequest(final HttpServletRequest request) {
        return request.getPathInfo().matches("/[0-9]+/state");
    }

    private void updateBundleStatus(final HttpServletRequest request, final Bundle bundle) throws IOException,
    BundleException, HttpErrorException {
        final BundleStatusRepresentation statusRepresentation = mapper.readValue(request.getReader(),
                BundleStatusRepresentation.class);

        if (statusRepresentation.getState() == Bundle.ACTIVE) {
            bundle.start();
        } else if (statusRepresentation.getState() == Bundle.RESOLVED) {
            bundle.stop();
        } else {
            throw new HttpErrorException("Requested state is not supported", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void updateBundle(final HttpServletRequest request, final Bundle bundle) throws IOException,
    BundleException, MalformedURLException {
        final String url = request.getReader().readLine();

        if (url == null || url.length() == 0) {
            bundle.update();
        } else {
            final InputStream stream = new URL(url).openStream();
            bundle.update(stream);
        }
    }

    private void updateBundleStartlevel(final HttpServletRequest request, final Bundle bundle) throws IOException {
        final BundleStartlevelRepresentation startlevelRepresentation = mapper.readValue(request.getReader(),
                BundleStartlevelRepresentation.class);

        final BundleStartLevel bundleStartLevel = bundle.adapt(BundleStartLevel.class);

        bundleStartLevel.setStartLevel(startlevelRepresentation.getStartLevel());
    }

    @Override
    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {

            final long bundleId = ServletUtil.getId(request);
            final Bundle bundle = ServletUtil.checkAndGetBundle(bundleContext, bundleId);
            bundle.uninstall();

        } catch (final HttpErrorException ex) {
            logger.error(ex.getMessage(), ex);
            response.sendError(ex.getHttpErrorCode());
        } catch (final BundleException ex) {
            logger.error("Could not uninstall Bundle", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String getBundle(final Bundle bundle) throws JsonProcessingException {

        final BundleRepresentation bundleRepresentation = new BundleRepresentation(bundle.getBundleId(),
                bundle.getLastModified(), bundle.getLocation(), bundle.getState(), bundle.getSymbolicName(),
                bundle.getSymbolicName());

        return mapper.writeValueAsString(bundleRepresentation);

    }

    private String getBundleStartlevel(final Bundle bundle) throws JsonProcessingException {
        final BundleStartLevel bundleStartLevel = bundle.adapt(BundleStartLevel.class);

        final BundleStartlevelRepresentation bundleStartlevelRepresentation = new BundleStartlevelRepresentation(
                bundleStartLevel.getStartLevel(), bundleStartLevel.isActivationPolicyUsed(),
                bundleStartLevel.isPersistentlyStarted());
        return mapper.writeValueAsString(bundleStartlevelRepresentation);
    }
}
