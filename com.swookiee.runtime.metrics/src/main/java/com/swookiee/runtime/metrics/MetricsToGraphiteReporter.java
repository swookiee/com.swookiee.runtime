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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.swookiee.runtime.metrics.configuration.GraphiteReporterConfiguration;
import com.swookiee.runtime.util.configuration.ConfigurationConsumer;

@Component(configurationPid = GraphiteReporterConfiguration.pid)
public class MetricsToGraphiteReporter {

    private static final Logger logger = LoggerFactory.getLogger(MetricsToGraphiteReporter.class);
    private static final String METRIC_PREFIX = "metricPrefix";

    /**
     * Instance where the reporter configuration will be held.
     */
    private ConfigurationConsumer<GraphiteReporterConfiguration> configurationConsumer = ConfigurationConsumer.withDefaultConfiguration(getDefaultConfiguration());;

    /**
     * Map of all MetricRegistry instances on the Service Registry. The value represents the reporter prefix
     */
    private Map<MetricRegistry, String> metricRegistries = new ConcurrentHashMap<>();

    /**
     * In case reporting is enabled a mapping between registry and reporter will be held. Remember
     * {@link ConcurrentHashMap} can not have <code>null</code> keys or values.
     */
    private Map<MetricRegistry, GraphiteReporter> startedReporter = new ConcurrentHashMap<>();

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
    public void addMetricRegistry(final MetricRegistry metricRegistry, Map<String, String> properties) {
        String prefix = "";
        if (properties.containsKey(METRIC_PREFIX)) {
            prefix = properties.get(METRIC_PREFIX);
        }

        this.metricRegistries.put(metricRegistry, prefix);

        if (isReportingEnabled()) {
            startAndMapReporterToRegistry(metricRegistry, prefix);
        }
    }

    public void removeMetricRegistry(final MetricRegistry metricRegistry) {
        if (this.startedReporter.containsKey(metricRegistry)) {
            this.startedReporter.get(metricRegistry).stop();
        }
        this.metricRegistries.remove(metricRegistry);
    }

    @Activate
    public void activate(final Map<String, Object> properties) {
        configurationConsumer.applyConfiguration(properties);
        startAllReporterIfEnabled();
    }

    @Modified
    public void modified(final Map<String, Object> properties) {
        stopAllGraphiteReporter();
        configurationConsumer.applyConfiguration(properties);
        startAllReporterIfEnabled();
    }

    @Deactivate
    public void deactivate(final BundleContext bundleContext) {
        stopAllGraphiteReporter();
    }

    private void stopAllGraphiteReporter() {
        for (Entry<MetricRegistry, GraphiteReporter> mapping : this.startedReporter.entrySet()) {
            mapping.getValue().stop();
            this.startedReporter.remove(mapping.getKey());
        }
    }

    private void startAllReporterIfEnabled() {
        if (isReportingEnabled()) {
            for (final Entry<MetricRegistry, String> metricRegistry : this.metricRegistries.entrySet()) {
                startAndMapReporterToRegistry(metricRegistry.getKey(), metricRegistry.getValue());
            }
        }
    }

    private void startAndMapReporterToRegistry(final MetricRegistry metricRegistry, final String prefix) {
        final GraphiteReporter startGraphiteReporter = startGraphiteReporter(metricRegistry, prefix);
        if (startGraphiteReporter != null) {
            this.startedReporter.put(metricRegistry, startGraphiteReporter);
        }
    }

    private GraphiteReporter startGraphiteReporter(final MetricRegistry metricRegistry, final String prefix) {

        final GraphiteReporterConfiguration configuration = configurationConsumer.getConfiguration(GraphiteReporterConfiguration.class);
        final Graphite graphite = new Graphite(new InetSocketAddress(configuration.graphiteHost,
                configuration.graphitePort));

        GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith(String.format("%s.%s.%s", configuration.reportingPrefix, getReverseHostName(), prefix))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);

        reporter.start(configuration.reportingIntervalInSeconds, TimeUnit.SECONDS);
        logger.info("Graphite Reporter started using configuration: {}", configuration);
        return reporter;
    }

    private boolean isReportingEnabled() {
        final GraphiteReporterConfiguration configuration = configurationConsumer.getConfiguration(GraphiteReporterConfiguration.class);

        if (configuration.graphiteHost == null || configuration.graphiteHost.isEmpty()) {
            return false;
        }
        return configuration.reportingEnabled;
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
            return "";
        }
    }
}
