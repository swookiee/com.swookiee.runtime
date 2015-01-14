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

package com.swookiee.runtime.metrics;

import static com.codahale.metrics.MetricRegistry.name;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

@Component
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class TimingResourceFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(TimingResourceFilter.class);

    private final static String HEADER_FIELD_NAME = "X-Processing-Time";

    private final Map<ContainerRequestContext, Timer.Context> resourceRequestTimers = new ConcurrentHashMap<>();

    private MetricRegistry metricRegistry;
    private Timer requestTimer;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final SwookieeMetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void unsetMetricRegistry(final SwookieeMetricRegistry metricRegistry) {
        this.metricRegistry = null;
    }

    @Activate
    public void activate() {
        logger.info("Activate Request Timer!");
        this.requestTimer = this.metricRegistry.timer(name(getClass(), "requests"));
    }

    @Deactivate
    public void deactivate() {
        resourceRequestTimers.clear();
        logger.info("Deactivated Request Timer!");
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        Timer timer = this.metricRegistry.timer(getResourceTimerName(requestContext));
        this.resourceRequestTimers.put(requestContext, timer.time());
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {
        Context resourceTimer = this.resourceRequestTimers.get(requestContext);

        if (resourceTimer == null) {
            return;
        }

        long elapsed = resourceTimer.stop();
        this.requestTimer.update(elapsed, TimeUnit.NANOSECONDS);
        responseContext.getHeaders().putSingle(HEADER_FIELD_NAME, elapsed);
        this.resourceRequestTimers.remove(requestContext);

    }

    public String getResourceTimerName(ContainerRequestContext requestContext) {
        try {
            UriRoutingContext routingContext = (UriRoutingContext) requestContext.getUriInfo();
            ResourceMethodInvoker invoker = (ResourceMethodInvoker) routingContext.getInflector();
            Class<?> clazz = invoker.getResourceClass();
            Method method = invoker.getResourceMethod();
            return name(clazz, method.getName());
        } catch (Exception ex) {
            return "undefined";
        }
    }
}
