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

import java.lang.management.ManagementFactory;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

@Component
public class JvmMetrics {

    private static final Logger logger = LoggerFactory.getLogger(JvmMetrics.class);
    private MetricRegistry metricRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final SwookieeMetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void unsetMetricRegistry(final SwookieeMetricRegistry metricRegistry) {
        this.metricRegistry = null;
    }

    @Activate
    public void activate(final BundleContext bundleContext) {
        registerAll("jvm.buffer-pools", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()),
                this.metricRegistry);
        registerAll("jvm.gc", new GarbageCollectorMetricSet(), this.metricRegistry);
        registerAll("jvm.memory", new MemoryUsageGaugeSet(), this.metricRegistry);
        registerAll("jvm.thread-states", new ThreadStatesGaugeSet(), this.metricRegistry);

        logger.info("Jvm Metrics activated!");
    }

    private void registerAll(final String prefix, final MetricSet metricSet, final MetricRegistry registry)
            throws IllegalArgumentException {
        for (final Map.Entry<String, Metric> entry : metricSet.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet) {
                registerAll(name(prefix, entry.getKey()), (MetricSet) entry.getValue(), registry);
            } else {
                registry.register(name(prefix, entry.getKey()), entry.getValue());
            }
        }
    }

    private String name(final String prefix, final String key) {
        return String.format("%s.%s", prefix, key);
    }

}