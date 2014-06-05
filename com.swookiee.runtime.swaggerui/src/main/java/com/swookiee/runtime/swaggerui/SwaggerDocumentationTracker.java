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

import javax.servlet.ServletException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link BundleTracker} is looking for the {@value SwaggerDocumentationTracker#SWAGGER_HEADER} header. In case it
 * is provided, it will register a {@link SwaggerDocumentationServlet} in order to provide its contents. It also adds a
 * references of the {@link Bundle} to the {@link SwaggerDocumentationRegistry}.
 */
public class SwaggerDocumentationTracker extends BundleTracker<Bundle> {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerDocumentationTracker.class);
    public static final String SWAGGER_HEADER = "X-Swagger-Documentation";
    private final HttpService httpService;
    private final SwaggerDocumentationRegistry documentationRegistry;

    public SwaggerDocumentationTracker(final BundleContext context, final HttpService httpService,
            final SwaggerDocumentationRegistry documentationRegistry) {
        super(context, Bundle.ACTIVE, null);
        this.httpService = httpService;
        this.documentationRegistry = documentationRegistry;
    }

    @Override
    public Bundle addingBundle(final Bundle bundle, final BundleEvent event) {
        final String swaggerPath = bundle.getHeaders().get(SWAGGER_HEADER);
        if (swaggerPath == null) {
            return bundle;
        }
        registerDocumentationServlet(bundle);
        return bundle;
    }

    @Override
    public void removedBundle(final Bundle bundle, final BundleEvent event, final Bundle object) {

        if (bundle.getHeaders().get(SWAGGER_HEADER) == null) {
            return;
        }
        this.documentationRegistry.unregister(bundle);
        this.httpService.unregister(getAlias(bundle));
    }

    private void registerDocumentationServlet(final Bundle bundle) {
        final SwaggerDocumentationServlet swaggerDocumentationServlet = new SwaggerDocumentationServlet(bundle);

        try {
            this.httpService.registerServlet(getAlias(bundle), swaggerDocumentationServlet, null, null);
            this.documentationRegistry.register(bundle);
        } catch (ServletException | NamespaceException ex) {
            logger.error("Could not register swagger documentation for bundle: {}, {}", bundle.getSymbolicName(),
                    ex.getMessage(), ex);
        }
    }

    private String getAlias(final Bundle bundle) {
        return MainComponent.SWAGGER_ALIAS + "/" + bundle.getSymbolicName();
    }
}
