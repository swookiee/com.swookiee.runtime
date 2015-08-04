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

import java.util.List;

/**
 * This interface marks a metric consisting of multiple Prometheus collectors. Implementations should be registered on
 * the OSGi Service Registry to be picked up and relayed to Prometheus by the MetricRegistry.
 * @see CollectorRegistryInventory
 */
public interface CollectorCollection {
    public List<Collector> getCollectors();
}