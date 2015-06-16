/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Ullrich - initial implementation
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JvmMetrics {

    private static final Logger logger = LoggerFactory.getLogger(JvmMetrics.class);
    private CollectorRegistry collectorRegistry;
    private final StandardExports standardExports = new StandardExports();
    private final MemoryPoolsExports memoryPoolsExports = new MemoryPoolsExports();
    private final GarbageCollectorExports garbageCollectorExports = new GarbageCollectorExports();

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final SwookieeCollectorRegistry collectorRegistry) {
        this.collectorRegistry = collectorRegistry;
    }

    public void unsetMetricRegistry(final SwookieeCollectorRegistry collectorRegistry) {
        this.collectorRegistry = null;
    }

    @Activate
    public void activate(final BundleContext bundleContext) {
        standardExports.register(collectorRegistry);
        memoryPoolsExports.register(collectorRegistry);
        garbageCollectorExports.register(collectorRegistry);
        logger.info("Jvm Metrics activated!");
    }

    @Deactivate
    public void deactivate() {
        collectorRegistry.unregister(standardExports);
        collectorRegistry.unregister(memoryPoolsExports);
        collectorRegistry.unregister(garbageCollectorExports);
        logger.info("Deactivated Jvm Metrics!");

    }
}
