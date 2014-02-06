package com.swookiee.runtime.metrics;

import static com.codahale.metrics.MetricRegistry.name;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

@Component
@Provider
public class TimingResourceFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(TimingResourceFilter.class);

    private final ConcurrentMap<ContainerRequestContext, Timer.Context> requestTiming = new ConcurrentHashMap<>();

    private MetricRegistry metricRegistry;
    private Timer requestTimer;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void unsetMetricRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = null;
    }

    @Activate
    public void activate() {
        this.requestTimer = this.metricRegistry.timer(name(getClass(), "requests"));
        logger.info("Request Timer activated!");
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        this.requestTiming.put(requestContext, this.requestTimer.time());
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {
        final Context context = this.requestTiming.get(requestContext);

        if (context == null) {
            return;
        }
        context.stop();
        this.requestTiming.remove(responseContext);
    }

}
