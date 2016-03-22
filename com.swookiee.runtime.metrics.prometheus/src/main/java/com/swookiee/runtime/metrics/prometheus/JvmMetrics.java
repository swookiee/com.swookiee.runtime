/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Ullrich - initial implementation
 *    Frank Wisniewski - switching to whiteboard pattern
 *    Lars Pfannenschmidt - added ThreadExports
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.hotspot.ThreadExports;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Component
public class JvmMetrics {

    private static final Logger logger = LoggerFactory.getLogger(JvmMetrics.class);
    private List<ServiceRegistration<Collector>> registeredCollectors = new ArrayList<>();
    private final StandardExports standardExports = new StandardExports();
    private final MemoryPoolsExports memoryPoolsExports = new MemoryPoolsExports();
    private final GarbageCollectorExports garbageCollectorExports = new GarbageCollectorExports();
    private final ThreadExports threadExports = new ThreadExports();

    @Activate
    public void activate(final BundleContext bundleContext) {
        registeredCollectors.add(bundleContext.registerService(Collector.class, standardExports, null));
        registeredCollectors.add(bundleContext.registerService(Collector.class, memoryPoolsExports, null));
        registeredCollectors.add(bundleContext.registerService(Collector.class, garbageCollectorExports, null));
        registeredCollectors.add(bundleContext.registerService(Collector.class, threadExports, null));
        logger.info("Jvm Metrics activated!");
    }

    @Deactivate
    public void deactivate() {
        for (ServiceRegistration<Collector> registration : registeredCollectors) {
            registration.unregister();
        }
        registeredCollectors.clear();
        logger.info("Deactivated Jvm Metrics!");

    }
}
