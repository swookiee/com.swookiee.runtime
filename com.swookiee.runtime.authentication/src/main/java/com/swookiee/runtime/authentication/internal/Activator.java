package com.swookiee.runtime.authentication.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    @Override
    public void start(final BundleContext bundleContext) {
        Activator.context = bundleContext;

    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        Activator.context = null;
    }
}
