/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Lars Pfannenschmidt - initial API and implementation, ongoing
 * development and documentation
 * *****************************************************************************
 */
package com.swookiee.runtime.prometheus;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;

@Component
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class TimingResourceFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(TimingResourceFilter.class);

    private final static String HEADER_FIELD_NAME = "X-Processing-Time";

    private static final Summary requestLatency = Summary.build()
            .name("requests_latency_seconds")
            .help("Request latency in seconds.")
            .labelNames("method", "resource")
            .create();

    private static final Histogram requestHistogram = Histogram.build()
            .name("requests_latency")
            .help("Request latency buckets.")
            .labelNames("method", "resource")
            .linearBuckets(0, 0.001, 5000)
            .create();

    private Summary.Timer summaryTimer;
    private Histogram.Timer histogramTimer;
    private Summary.Child requestLatencyChild;
    private Histogram.Child requestHistogramChild;

    private CollectorRegistry collectorRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final SwookieeCollectorRegistry collectorRegistry) {
        this.collectorRegistry = collectorRegistry;
    }

    public void unsetMetricRegistry(final SwookieeCollectorRegistry collectorRegistry) {
        this.collectorRegistry = null;
    }

    @Activate
    public void activate() {
        logger.info("Activate Request Timer!");
        requestLatency.register(collectorRegistry);
        requestHistogram.register(collectorRegistry);

    }

    @Deactivate
    public void deactivate() {
        collectorRegistry.unregister(requestLatency);
        collectorRegistry.unregister(requestHistogram);
        logger.info("Deactivated Request Timer!");
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        summaryTimer = requestLatency
                .labels(
                        requestContext.getMethod(),
                        getResourceTimerName(requestContext)
                ).startTimer();
        histogramTimer = requestHistogram.
                labels(
                        requestContext.getMethod(),
                        getResourceTimerName(requestContext)
                ).startTimer();
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {
        summaryTimer.observeDuration();
        histogramTimer.observeDuration();
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
