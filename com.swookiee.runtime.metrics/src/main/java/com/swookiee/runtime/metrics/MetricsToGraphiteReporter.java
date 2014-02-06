package com.swookiee.runtime.metrics;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

@Component
public class MetricsToGraphiteReporter {

    private static final Logger logger = LoggerFactory.getLogger(MetricsToGraphiteReporter.class);

    private MetricRegistry metricRegistry;

    private GraphiteReporter reporter;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void unsetMetricRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = null;
    }

    @Activate
    public void activate(final BundleContext bundleContext) {

        // FIXME configure via config admin
        final String graphiteHost = System.getProperty("graphiteHost");
        final Integer graphitePort = Integer.getInteger("graphitePort", 2003);

        if (graphiteHost == null) {
            logger.info("Graphite Reporter activated but no remote endpoint configured!");
            return;
        }

        startGraphiteReporter(graphiteHost, graphitePort);

    }

    private void startGraphiteReporter(final String graphiteHost, final Integer graphitePort) {
        final Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort));

        reporter = GraphiteReporter.forRegistry(this.metricRegistry)
                .prefixedWith(getReverseHostName())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);

        reporter.start(1, TimeUnit.MINUTES);

        logger.info("Graphite Reporter started using endpoint: {}:{}", graphiteHost, graphitePort);
    }

    @Deactivate
    public void deactivate(final BundleContext bundleContext) {
        if (reporter != null) {
            reporter.stop();
        }
    }

    private String getReverseHostName() {
        try {
            final String hostName = InetAddress.getLocalHost().getHostName();
            final String[] split = hostName.split("\\.");

            final StringBuilder sb = new StringBuilder();

            for (int i = split.length - 1; i >= 0; i--) {
                sb.append(split[i]);
                if (i != 0) {
                    sb.append('.');
                }
            }

            return sb.toString();

        } catch (final UnknownHostException ex) {
            logger.error("Could not obtain hostname from system, weird", ex);
            return null;
        }
    }
}
