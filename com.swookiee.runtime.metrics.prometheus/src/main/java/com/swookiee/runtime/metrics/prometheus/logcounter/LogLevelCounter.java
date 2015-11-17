/**
 * *****************************************************************************
 * Copyright (c) 2015 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lars Pfannenschmidt - initial implementation
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus.logcounter;

import io.prometheus.client.Collector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LogLevelCounter {
    private static final Logger logger = LoggerFactory.getLogger(LogLevelCounter.class);
    private ServiceRegistration<Collector> registerdCounter;
    private LogLevelCounterAppender instrumentedAppender;
    private ch.qos.logback.classic.Logger rootLogger;

    @Activate
    public void activate(BundleContext bundleContext) {
        logger.info("Starting Prometheus Log Level Counter!");
        rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("root");
        instrumentedAppender = new LogLevelCounterAppender();
        instrumentedAppender.start();
        rootLogger.addAppender(instrumentedAppender);
        registerdCounter = bundleContext.registerService(Collector.class, instrumentedAppender.getCounter(), null);
    }

    @Deactivate
    public void deactivate() {
        instrumentedAppender.stop();
        rootLogger.detachAppender(instrumentedAppender);
        registerdCounter.unregister();
        logger.info("Stopped Prometheus Log Level Counter!");
    }
}
