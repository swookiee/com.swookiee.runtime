package com.swookiee.runtime.swaggerui;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * This JAX-RS resource provides a list of registered swagger documentation bundles. It is used to render the swagger-ui
 * properly.
 */
@Component(service = {SwaggerDocumentationResource.class})
@Path(SwaggerDocumentationResource.PATH)
@Produces(APPLICATION_JSON)
public class SwaggerDocumentationResource {

    public static final String PATH = "swagger-documentaion";
    private SwaggerDocumentationRegistry swaggerDocumentationRegistry;

    @GET
    public List<String> getAPIDocumentationBundles() {
        List<Bundle> registeredBundles = swaggerDocumentationRegistry.getRegisteredBundles();
        return Lists.transform(registeredBundles, new BundleSymbolicNameFunction());
    }

    @Reference
    public void setSwaggerDocumentationRegistry(SwaggerDocumentationRegistry swaggerDocumentationRegistry) {
        this.swaggerDocumentationRegistry = swaggerDocumentationRegistry;
    }

    public void unsetSwaggerDocumentationRegistry(SwaggerDocumentationRegistry swaggerDocumentationRegistry) {
        this.swaggerDocumentationRegistry = null;
    }

    private class BundleSymbolicNameFunction implements Function<Bundle, String> {
        @Override
        public String apply(Bundle bundle) {
            return bundle.getSymbolicName();
        }
    }
}
