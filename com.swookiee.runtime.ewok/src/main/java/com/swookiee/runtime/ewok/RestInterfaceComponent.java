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

package com.swookiee.runtime.ewok;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swookiee.runtime.authentication.AuthenticationService;
import com.swookiee.runtime.ewok.auth.BasicAuthHttpContext;
import com.swookiee.runtime.ewok.servlet.BundleServlet;
import com.swookiee.runtime.ewok.servlet.BundlesRepresentationsServlet;
import com.swookiee.runtime.ewok.servlet.BundlesServlet;
import com.swookiee.runtime.ewok.servlet.FrameworkStartLevelServlet;
import com.swookiee.runtime.ewok.servlet.ServiceServlet;
import com.swookiee.runtime.ewok.servlet.ServicesServlet;

/**
 * This class is the main component registering RFC-182 endpoints. @see <a
 * href=https://github.com/osgi/design/tree/master/rfcs/rfc0182>
 * https://github.com/osgi/design/tree/master/rfcs/rfc0182</a>
 */
@Component
public class RestInterfaceComponent {

    private final Map<String, HttpServlet> servlets = new HashMap<>();

    private BundleContext bundleContext;
    private HttpService httpService;
    private AuthenticationService authenticationService;

    private static final Logger logger = LoggerFactory.getLogger(RestInterfaceComponent.class);

    public void activate(final ComponentContext context) {
        bundleContext = context.getBundleContext();
        registerServletsToHttpService();
        logger.info("RestInterfaceComponent activated!");
    }

    public void deactivate(final ComponentContext context) {
        unregisterServlets();
        logger.info("RestInterfaceComponent deactivated!");
    }

    private void unregisterServlets() {
        for (final String alias : servlets.keySet()) {
            httpService.unregister(alias);
        }
    }

    private void registerServletsToHttpService() {
        servlets.put(FrameworkStartLevelServlet.ALIAS, new FrameworkStartLevelServlet(this.bundleContext));
        servlets.put(BundlesServlet.ALIAS, new BundlesServlet(this.bundleContext));
        servlets.put(BundlesRepresentationsServlet.ALIAS, new BundlesRepresentationsServlet(this.bundleContext));
        servlets.put(BundleServlet.ALIAS, new BundleServlet(this.bundleContext));
        servlets.put(ServicesServlet.ALIAS, new ServicesServlet(this.bundleContext));
        servlets.put(ServiceServlet.ALIAS, new ServiceServlet(this.bundleContext));

        final BasicAuthHttpContext basicAuthHttpContext = new BasicAuthHttpContext(authenticationService);

        try {
            for (final String alias : servlets.keySet()) {
                httpService.registerServlet(alias, servlets.get(alias), null, basicAuthHttpContext);
            }

        } catch (ServletException | NamespaceException ex) {
            logger.error("Servlet could not be registred: {}", ex.getMessage(), ex);
        }
    }

    @Reference
    public void setAuthenticationService(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void unsetAuthenticationService(final AuthenticationService authenticationService) {
        this.authenticationService = null;
    }

    @Reference
    public void setHttpService(final HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsetHttpService(final HttpService httpService) {
        this.httpService = null;
    }

}
