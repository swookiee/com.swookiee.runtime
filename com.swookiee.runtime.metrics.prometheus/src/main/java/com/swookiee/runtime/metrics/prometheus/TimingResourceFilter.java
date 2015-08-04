/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Ullrich - initial implementation
 *    Frank Wisniewski - changed registration to white board pattern
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.Summary;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class TimingResourceFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(TimingResourceFilter.class);

    private final static String HEADER_FIELD_NAME = "X-Processing-Time";

    private final Map<ContainerRequestContext, Long> resourceRequestTimers = new ConcurrentHashMap<>();
    private ServiceRegistration<Collector> registeredLatency;

    private static final Summary requestLatency = Summary.build()
            .name("requests_latency_seconds")
            .help("Request latency in seconds.")
            .labelNames("method", "resource", "status")
            .create();

    @Activate
    public void activate(final BundleContext bundleContext) {
        logger.info("Activate Request Timer!");
        registeredLatency = bundleContext.registerService(Collector.class, requestLatency, null);
    }

    @Deactivate
    public void deactivate() {
        registeredLatency.unregister();
        logger.info("Deactivated Request Timer!");
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        this.resourceRequestTimers.put(requestContext, System.nanoTime());
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {
        Long startTime = this.resourceRequestTimers.get(requestContext);

        if (startTime == null) {
            return;
        }

        this.resourceRequestTimers.remove(requestContext);

        double elapsed = ((double) (System.nanoTime() - startTime)) / 1000000.0;

        responseContext.getHeaders().putSingle(HEADER_FIELD_NAME, elapsed);

        String responseStatus = Integer.toString(responseContext.getStatus());
        requestLatency.labels(requestContext.getMethod(), getResourceTimerName(requestContext), responseStatus)
                .observe(elapsed);
    }

    public String getResourceTimerName(ContainerRequestContext requestContext) {
        try {
            UriRoutingContext routingContext = (UriRoutingContext) requestContext.getUriInfo();
            ResourceMethodInvoker invoker = (ResourceMethodInvoker) routingContext.getEndpoint();
            Class<?> clazz = invoker.getResourceClass();
            Method method = invoker.getResourceMethod();
            return String.format("%s.%s", clazz.getSimpleName(), method.getName());
        } catch (Exception ex) {
            return "undefined.undefined";
        }
    }
}
