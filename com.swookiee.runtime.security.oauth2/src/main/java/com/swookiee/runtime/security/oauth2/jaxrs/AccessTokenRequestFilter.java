package com.swookiee.runtime.security.oauth2.jaxrs;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Provider
public class AccessTokenRequestFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenRequestFilter.class);

    private JsonTokenValidator jsonTokenValidator;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        MultivaluedMap<String, String> headers = containerRequestContext.getHeaders();
        List<String> authorizationHeaders = headers.get("Authorization");

        if (authorizationHeaders == null) {
            logger.warn("No authorization header specified.");
            containerRequestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
            return;
        }

        for (String authorizationHeader : authorizationHeaders) {
            if (authorizationHeader.startsWith("Bearer")) {
                try {
                    jsonTokenValidator.validate(authorizationHeader);
                } catch (JsonTokenValidationException ex) {
                    logger.warn("Could not validate token: " + ex.getMessage(), ex);
                    containerRequestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
                    return;
                }
            } else {
                logger.warn("Authorization header does not start with 'Bearer'.");
                containerRequestContext.abortWith(Response.status(Status.BAD_REQUEST).build());
                return;
            }
        }
    }

    @Reference
    public void setJsonTokenValidator(JsonTokenValidator jsonTokenValidator) {
        this.jsonTokenValidator = jsonTokenValidator;
    }

    public void unsetJsonTokenValidator(JsonTokenValidator jsonTokenValidator) {
        this.jsonTokenValidator = null;
    }

}
