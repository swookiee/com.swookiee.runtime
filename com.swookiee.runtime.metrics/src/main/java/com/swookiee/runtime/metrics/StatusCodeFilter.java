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

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

@Component
@Provider
public class StatusCodeFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(StatusCodeFilter.class);
    private MetricRegistry metricRegistry;
    private final ConcurrentMap<Integer, Meter> statusCodeMeters = new ConcurrentHashMap<>();


    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final SwookieeMetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void unsetMetricRegistry(final SwookieeMetricRegistry metricRegistry) {
        this.metricRegistry = null;
    }

    @Activate
    public void activate() {
        logger.info("Activate Status Code Filter!");
    }

    @Deactivate
    public void deactivate() {
        this.statusCodeMeters.clear();
        logger.info("Deactivated Status Code Filter!");
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {
        markMeterForStatusCode(responseContext.getStatus());
    }

    private void markMeterForStatusCode(final int status) {
        final Meter meter = statusCodeMeters.get(status);
        if (meter != null) {
            meter.mark();
        } else {
            initMeterForStatus(status);
            markMeterForStatusCode(status);
        }
    }

    private void initMeterForStatus(final int status) {
        final Meter meter = metricRegistry.meter(String.format("StatusCodes.%d", status));
        statusCodeMeters.put(status, meter);
    }
}
