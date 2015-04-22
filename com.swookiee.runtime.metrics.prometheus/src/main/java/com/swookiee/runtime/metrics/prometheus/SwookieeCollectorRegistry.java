/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Lars Pfannenschmidt - initial API and implementation, ongoing
 * development and documentation
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus;

import io.prometheus.client.CollectorRegistry;
import org.osgi.service.component.annotations.Component;

@Component(service = {CollectorRegistry.class, SwookieeCollectorRegistry.class}, property = {"metricPrefix=swookiee"})
public class SwookieeCollectorRegistry extends CollectorRegistry {

}
