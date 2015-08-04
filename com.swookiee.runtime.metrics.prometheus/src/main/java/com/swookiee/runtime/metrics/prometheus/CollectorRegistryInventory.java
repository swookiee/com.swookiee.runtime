/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Wisniewski - initial implementation
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component(service = {CollectorRegistryInventory.class})
public class CollectorRegistryInventory implements BundleListener {

    private static final Logger logger = LoggerFactory.getLogger(CollectorRegistryInventory.class);
    private static final String BUNDLE_ID = "service.bundleid";
    private BundleContext bundleContext;

    private Map<String, CollectorRegistry> registeredRegistries = new ConcurrentHashMap<>();
    private Function<String, CollectorRegistry> registryInstantiator = new Function<String, CollectorRegistry>() {
        @Override
        public CollectorRegistry apply(String bundle) {
            return new CollectorRegistry();
        }
    };

    @Activate
    public void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.bundleContext.addBundleListener(this);
        logger.info("Activated metric collector!");
    }

    @Deactivate
    public void deactivate() {
        for (CollectorRegistry registry : registeredRegistries.values()) {
            registry.clear();
        }
        registeredRegistries.clear();
        logger.info("Deactivated metric collector!");
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void registerCollection(CollectorCollection collectorCollection, Map<String, Object> properties) {
        CollectorRegistry registry = getRegistry(properties);
        for (Collector collector : collectorCollection.getCollectors()) {
            registry.register(collector);
        }
    }

    public void unregisterCollection(CollectorCollection collectorCollection, Map<String, Object> properties) {
        CollectorRegistry registry = getRegistry(properties);
        for (Collector collector : collectorCollection.getCollectors()) {
            registry.unregister(collector);
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void registerCollector(Collector metricsCollection, Map<String, Object> properties) {
        getRegistry(properties).register(metricsCollection);
    }

    public void unregisterCollector(Collector metricsCollection, Map<String, Object> properties) {
        getRegistry(properties).unregister(metricsCollection);
    }

    public CollectorRegistry getCollectorRegistry(String bundle) {
        return registeredRegistries.get(bundle);
    }

    public List<String> getRegisteredBundles() {
        return new ArrayList<>(registeredRegistries.keySet());
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        if (BundleEvent.STOPPED != event.getType()) {
            return;
        }
        remove(event.getBundle().getSymbolicName());
    }

    private void remove(String bundle) {
        if (!registeredRegistries.containsKey(bundle)) {
            return;
        }

        CollectorRegistry registry = registeredRegistries.get(bundle);
        registry.clear();
        registeredRegistries.remove(bundle);
        logger.debug("Removed bundle {} from MetricRegistry", bundle);
    }

    private CollectorRegistry getRegistry(Map<String, Object> properties) {
        String bundle = getBundleName(properties);
        return registeredRegistries.computeIfAbsent(bundle, registryInstantiator);
    }

    private String getBundleName(Map<String, Object> properties) {
        long bundleId = (long) properties.get(BUNDLE_ID);
        Bundle bundle = bundleContext.getBundle(bundleId);
        return bundle.getSymbolicName();
    }
}