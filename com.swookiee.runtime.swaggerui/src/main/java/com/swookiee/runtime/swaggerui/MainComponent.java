package com.swookiee.runtime.swaggerui;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This component orchestrates all necessary components to provide static swagger-ui content, tracking swagger
 * documentation bundles and serving their contents.
 */
@Component
public class MainComponent {

    static final String SWAGGER_ALIAS = "/swagger";
    private static final String RESOURCES_FOLDER = "/tools";
    private static final Logger logger = LoggerFactory.getLogger(MainComponent.class);

    private HttpService httpService;
    private SwaggerDocumentationRegistry documentationRegistry;
    private SwaggerDocumentationTracker documentationTracker;

    @Activate
    private void activate(final BundleContext bundleContext) {
        startDocumentationTracker(bundleContext);
        regsiterSwaggerResources(bundleContext);
    }

    private void regsiterSwaggerResources(final BundleContext bundleContext) {
        try {
            this.httpService.registerResources(SWAGGER_ALIAS, RESOURCES_FOLDER, null);

        } catch (final NamespaceException ex) {
            logger.error("Swagger UI resource path {} already in use: {}", SWAGGER_ALIAS, ex.getMessage(), ex);
        }
    }

    @Deactivate
    private void deactivate() {
        documentationTracker.close();
        httpService.unregister(SWAGGER_ALIAS);
    }

    private void startDocumentationTracker(final BundleContext bundleContext) {
        documentationTracker = new SwaggerDocumentationTracker(bundleContext, this.httpService,
                this.documentationRegistry);
        documentationTracker.open();
    }

    @Reference
    public void setDocumentationRegistry(SwaggerDocumentationRegistry documentationRegistry) {
        this.documentationRegistry = documentationRegistry;
    }

    public void unsetDocumentationRegistry(SwaggerDocumentationRegistry documentationRegistry) {
        this.documentationRegistry = null;
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MANDATORY)
    public void setHttpService(final HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsetHttpService(final HttpService httpService) {
        this.httpService = null;
    }
}
