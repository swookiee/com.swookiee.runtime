package com.swookiee.core.internal.logging;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This needs to be realized via a ServiceTracker in order to decouple from other dependencies such as Declarative
 * Services since this Bundle needs to be started as one of the very firsts. Otherwise log messages could get lost.
 * 
 */
public class LogReaderServiceTracker implements ServiceTrackerCustomizer<LogReaderService, LogReaderService> {

    private final BundleContext bundleContext;
    private final Map<LogReaderService, LogForwarder> logReaderServices = new HashMap<>();

    public LogReaderServiceTracker(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public LogReaderService addingService(final ServiceReference<LogReaderService> reference) {

        final LogForwarder listener = new LogForwarder();
        final LogReaderService logReaderService = bundleContext.getService(reference);
        this.logReaderServices.put(logReaderService, listener);
        logReaderService.addLogListener(listener);

        return logReaderService;
    }

    @Override
    public void modifiedService(final ServiceReference<LogReaderService> reference, final LogReaderService service) {
        // nothing to do here
    }

    @Override
    public void removedService(final ServiceReference<LogReaderService> reference, final LogReaderService service) {
        this.bundleContext.ungetService(reference);
        logReaderServices.remove(service);
    }

    private static class LogForwarder implements LogListener {
        @Override
        public void logged(final LogEntry entry) {

            final Logger logger = LoggerFactory.getLogger("Bundle." + entry.getBundle().getSymbolicName());
            // Marker marker = MarkerFactory.getMarker(entry.getBundle().getSymbolicName());

            switch (entry.getLevel()) {
            case LogService.LOG_DEBUG:
                logger.debug(entry.getMessage(), entry.getException());
                break;
            case LogService.LOG_INFO:
                logger.info(entry.getMessage(), entry.getException());
                break;
            case LogService.LOG_WARNING:
                logger.warn(entry.getMessage(), entry.getException());
                break;
            case LogService.LOG_ERROR:
                logger.error(entry.getMessage(), entry.getException());
                break;
            }
        }
    }
}
