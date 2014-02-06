package com.swookiee.core;

import java.util.logging.Handler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * This component redirects OSGi Log Messages to the slf4j facade.
 * <p>
 * Inspired from:
 * http://ekkescorner.wordpress.com/2009/09/05/osgi-logging-part-5-listen-to-osgi-log-reader-with-declarative-services-
 * ds/
 * <p>
 * & https://github.com/openhab/openhab/blob/master/bundles/core/org.openhab.core/src/main/java/org/openhab/core/
 * internal/ logging/OSGILogListener.java
 * 
 */
public class OSGiLogForwarderActivator implements BundleActivator {

    private ServiceTracker<LogReaderService, LogReaderService> serviceTracker;

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        final java.util.logging.Logger rootLogger = java.util.logging.LogManager.getLogManager().getLogger("");
        final Handler[] handlers = rootLogger.getHandlers();
        for (final Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        SLF4JBridgeHandler.install();

        final LogReaderServiceTracker customer = new LogReaderServiceTracker(bundleContext);
        serviceTracker = new ServiceTracker<>(bundleContext, LogReaderService.class.getName(), customer);
        serviceTracker.open();

    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        serviceTracker.close();
    }


}
