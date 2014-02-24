package com.swookiee.runtime.swaggerui;

import java.util.List;

import javax.servlet.ServletException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwaggerDocumentationTracker extends BundleTracker<Bundle> {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerDocumentationTracker.class);

    private final HttpService httpService;
    private final List<Bundle> apiBundles;

    public SwaggerDocumentationTracker(final BundleContext context, final HttpService httpService,
            final List<Bundle> apiBundles) {
        super(context, Bundle.ACTIVE, null);
        this.httpService = httpService;
        this.apiBundles = apiBundles;
    }

    @Override
    public Bundle addingBundle(final Bundle bundle, final BundleEvent event) {

        final String swaggerPath = bundle.getHeaders().get("X-Swagger-Documentation");

        if (swaggerPath == null) {
            return bundle;
        }

        registerDocumentationServlet(bundle);

        return bundle;

    }

    @Override
    public void removedBundle(final Bundle bundle, final BundleEvent event, final Bundle object) {

        if (bundle.getHeaders().get("X-Swagger-Documentation")== null) {
            return;
        }
        this.apiBundles.remove(bundle);
        this.httpService.unregister(getAlias(bundle));
    }

    private void registerDocumentationServlet(final Bundle bundle) {
        final SwaggerDocumentationServlet swaggerDocumentationServlet = new SwaggerDocumentationServlet(bundle);

        try {
            this.httpService.registerServlet(getAlias(bundle), swaggerDocumentationServlet, null, null);
            apiBundles.add(bundle);
        } catch (ServletException | NamespaceException ex) {
            logger.error("Could not register swagger documentation for bundle: {}, {}", bundle.getSymbolicName(),
                    ex.getMessage(), ex);
        }
    }

    private String getAlias(final Bundle bundle) {
        return HttpComponentRegistration.SWAGGER_ALIAS + "/" + bundle.getSymbolicName();
    }

    @Override
    public void close() {
        for (final Bundle bundle : apiBundles) {
            this.httpService.unregister(getAlias(bundle));
        }
        super.close();
    }
}
