/*******************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt, Thorsten Krüger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lars Pfannenschmidt - initial API and implementation, ongoing development and documentation
 *    Thorsten Krüger - added JSON logging activation
 *******************************************************************************/

package com.swookiee.runtime.core.internal;

import java.util.logging.Handler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.swookiee.runtime.core.internal.logging.JsonFileLoggingDecorator;
import com.swookiee.runtime.core.internal.logging.LogReaderServiceTracker;

/**
 * This Activator initializes the logging from all various sources to the slf4j facade.
 * <p>
 * Inspired from:
 * http://ekkescorner.wordpress.com/2009/09/05/osgi-logging-part-5-listen-to-osgi-log-reader-with-declarative-services-
 * ds/
 * <p>
 * &amp; https://github.com/openhab/openhab/blob/master/bundles/core/org.openhab.core/src/main/java/org/openhab/core/
 * internal/ logging/OSGILogListener.java
 */
public class Activator implements BundleActivator {

    private static BundleContext context;

    private ServiceTracker<LogReaderService, LogReaderService> serviceTracker;

    static BundleContext getContext() {
        return context;
    }

    @Override
    public void start(final BundleContext bundleContext) {
        Activator.context = bundleContext;

        redirectLoggingHandlers();
        redirectOSGiLogging();
        installLogbackDeploymentLogging();
    }

    private void redirectLoggingHandlers() {
        final java.util.logging.Logger rootLogger = java.util.logging.LogManager.getLogManager().getLogger("");

        final Handler[] handlers = rootLogger.getHandlers();
        for (final Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        SLF4JBridgeHandler.install();
    }

    private void redirectOSGiLogging() {
        final LogReaderServiceTracker customer = new LogReaderServiceTracker(Activator.context);
        serviceTracker = new ServiceTracker<>(Activator.context, LogReaderService.class.getName(), customer);
        serviceTracker.open();
    }

    private void installLogbackDeploymentLogging() {
        new JsonFileLoggingDecorator().install(context);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        serviceTracker.close();
        Activator.context = null;
    }
}
