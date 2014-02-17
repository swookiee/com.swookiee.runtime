package com.swookiee.runtime.metrics;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.swookiee.core.configuration.ConfigurationConsumer;
import com.swookiee.runtime.metrics.configuration.GraphiteReporterConfiguration;

@Component(configurationPid = GraphiteReporterConfiguration.pid)
public class MetricsToGraphiteReporter {

    private static final Logger logger = LoggerFactory.getLogger(MetricsToGraphiteReporter.class);

    private MetricRegistry metricRegistry;
    private GraphiteReporter reporter;
    private ConfigurationConsumer<GraphiteReporterConfiguration> configurationConsumer;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void unsetMetricRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = null;
    }

    @Activate
    public void activate(final Map<String, Object> properties) {
        configurationConsumer = ConfigurationConsumer.withDefaultConfiguration(getDefaultConfiguration());
        configurationConsumer.applyConfiguration(properties);
        final GraphiteReporterConfiguration configuration = configurationConsumer.getConfiguration(GraphiteReporterConfiguration.class);

        startGraphiteReporter(configuration);
    }

    @Modified
    public void modified(final Map<String, Object> properties) {
        reporter.stop();

        configurationConsumer.applyConfiguration(properties);
        final GraphiteReporterConfiguration configuration = configurationConsumer.getConfiguration(GraphiteReporterConfiguration.class);

        startGraphiteReporter(configuration);
    }

    private void startGraphiteReporter(final GraphiteReporterConfiguration configuration) {

        if (!configuration.reportingEnabled) {
            logger.debug("Graphite Reporter is disabled");
            return;
        }

        if (configuration.graphiteHost == null) {
            logger.info("Graphite Reporter could not be started, no host configured");
            return;
        }

        final Graphite graphite = new Graphite(new InetSocketAddress(configuration.graphiteHost,
                configuration.graphitePort));

        reporter = GraphiteReporter.forRegistry(this.metricRegistry)
                .prefixedWith(getReverseHostName())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);

        reporter.start(configuration.reportingIntervalInSeconds, TimeUnit.SECONDS);

        logger.info("Graphite Reporter started using configuration: {}", configuration);
    }

    @Deactivate
    public void deactivate(final BundleContext bundleContext) {
        stopGraphiteReporter();
    }

    private void stopGraphiteReporter() {
        if (reporter != null) {
            reporter.stop();
        }
    }

    private GraphiteReporterConfiguration getDefaultConfiguration() {

        final GraphiteReporterConfiguration config = new GraphiteReporterConfiguration();
        config.graphitePort = 2003;
        config.reportingEnabled = false;
        config.reportingIntervalInSeconds = 60;

        return config;
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
