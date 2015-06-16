/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Ullrich - initial implementation
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import java.lang.reflect.Method;
import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;

@Component
@Provider
public class StatusCodeFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(StatusCodeFilter.class);
    private CollectorRegistry collectorRegistry;

    private static final Counter requestCounter = Counter.build()
            .name("requests_total")
            .help("Request counters.")
            .labelNames("status", "method", "resource")
            .create();

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final SwookieeCollectorRegistry collectorRegistry) {
        this.collectorRegistry = collectorRegistry;
    }

    public void unsetMetricRegistry(final SwookieeCollectorRegistry collectorRegistry) {
        this.collectorRegistry = null;
    }

    @Activate
    public void activate() {
        requestCounter.register(collectorRegistry);
        logger.info("Activate Status Code Filter!");
    }

    @Deactivate
    public void deactivate() {
        collectorRegistry.unregister(requestCounter);
        logger.info("Deactivated Status Code Filter!");
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {
        requestCounter.labels(
                Integer.toString(responseContext.getStatus()),
                requestContext.getMethod(),
                getResourceTimerName(requestContext))
                .inc();
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
