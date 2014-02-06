package com.swookiee.runtime.metrics;

import static com.codahale.metrics.MetricRegistry.name;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
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

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void unsetMetricRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = null;
    }

    private ConcurrentMap<Integer, Meter> statusCodeMeters;

    @Activate
    public void activate() {

        statusCodeMeters = new ConcurrentHashMap<>();
        logger.info("Status Code Filter activated!");
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
        final Meter meter = metricRegistry.meter(name(getClass(), String.format("response.%d", status)));
        statusCodeMeters.put(status, meter);
    }
}
