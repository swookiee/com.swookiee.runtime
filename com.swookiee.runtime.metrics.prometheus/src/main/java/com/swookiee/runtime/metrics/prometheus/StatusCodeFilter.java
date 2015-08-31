/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Ullrich - initial implementation
 *    Frank Wisniewski - switching to whiteboard pattern
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.Counter;
import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Component
@Provider
public class StatusCodeFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(StatusCodeFilter.class);
    private ServiceRegistration<Collector> registeredCounter;

    private static final Counter requestCounter = Counter.build()
            .name("requests_total")
            .help("Request counters.")
            .labelNames("status", "method", "resource")
            .create();

    @Activate
    public void activate(final BundleContext bundleContext) {
        registeredCounter = bundleContext.registerService(Collector.class, requestCounter, null);
        logger.info("Activate Status Code Filter!");
    }

    @Deactivate
    public void deactivate() {
        registeredCounter.unregister();
        logger.info("Deactivated Status Code Filter!");
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {
        requestCounter.labels(Integer.toString(responseContext.getStatus()), requestContext.getMethod(),
                getResourceTimerName(requestContext)).inc();
    }

    public String getResourceTimerName(ContainerRequestContext requestContext) {
        try {
            UriRoutingContext routingContext = (UriRoutingContext) requestContext.getUriInfo();
            ResourceMethodInvoker invoker = (ResourceMethodInvoker) routingContext.getEndpoint();
            Class<?> clazz = invoker.getResourceClass();
            Method method = invoker.getResourceMethod();
            return String.format("%s.%s", clazz.getSimpleName(), method.getName());
        } catch (Exception ex) {
            return "undefined";
        }
    }
}
