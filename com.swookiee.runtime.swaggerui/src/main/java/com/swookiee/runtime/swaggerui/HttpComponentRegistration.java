package com.swookiee.runtime.swaggerui;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import org.osgi.framework.Bundle;
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

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

@Component
public class HttpComponentRegistration {

    static final String SWAGGER_ALIAS = "/apidocs";
    private static final String MAIN_ALIAS = SWAGGER_ALIAS + "/api";
    private static final String RESOURCES_FOLDER = "/resources";
    private static final String SWAGGER_INDEX_HTML = "resources/index.html";
    private static final Logger logger = LoggerFactory.getLogger(HttpComponentRegistration.class);

    private HttpService httpService;
    private SwaggerDocumentationTracker swaggerDocumentationTracker;
    private final List<Bundle> apiBundles = Collections.synchronizedList(new ArrayList<Bundle>());

    @Activate
    private void activate(final BundleContext bundleContext) {
        startDocumentationTracker(bundleContext);
        regsiterSwaggerResources(bundleContext);
    }

    private void regsiterSwaggerResources(final BundleContext bundleContext) {
        try {
            this.httpService.registerResources(SWAGGER_ALIAS, RESOURCES_FOLDER, null);

            final String swaggerPage = readSwaggerIndexFile(bundleContext);
            this.httpService.registerServlet(MAIN_ALIAS, new SwaggerIndexServlet(swaggerPage, this.apiBundles), null,
                    null);

        } catch (final NamespaceException ex) {
            logger.error("Swagger UI resource path {} already in use: {}", SWAGGER_ALIAS, ex.getMessage(), ex);
        } catch (final ServletException ex) {
            logger.error("Could not register swagger servlet: {}", SWAGGER_ALIAS, ex);
        } catch (final IOException ex) {
            logger.error("Could not read swagger ui base file: {}", ex.getMessage(), ex);
        }
    }

    private String readSwaggerIndexFile(final BundleContext bundleContext) throws IOException {
        final URL resource = bundleContext.getBundle().getResource(SWAGGER_INDEX_HTML);
        final String swaggerPage = CharStreams.toString(new InputStreamReader(resource.openStream(), Charsets.UTF_8));
        return swaggerPage;
    }

    @Deactivate
    private void deactivate() {
        httpService.unregister(SWAGGER_ALIAS);
        swaggerDocumentationTracker.close();
    }

    private void startDocumentationTracker(final BundleContext bundleContext) {
        swaggerDocumentationTracker = new SwaggerDocumentationTracker(bundleContext, this.httpService, this.apiBundles);
        swaggerDocumentationTracker.open();
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MANDATORY)
    public void setHttpService(final HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsetHttpService(final HttpService httpService) {
        this.httpService = null;
    }
}
