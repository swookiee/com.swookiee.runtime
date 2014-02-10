package com.swookiee.runtime.authentication.internal;

import org.apache.felix.webconsole.WebConsoleSecurityProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.swookiee.runtime.authentication.AuthenticationService;

@Component
public class SwookieeWebConsoleSecurityProvider implements WebConsoleSecurityProvider {

    private AuthenticationService authenticationService;

    @Reference
    public void setAuthenticationService(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void unsetAuthenticationService(final AuthenticationService authenticationService) {
        this.authenticationService = null;
    }


    @Override
    public Object authenticate(final String username, final String password) {
        final boolean validateUserCredentials = authenticationService.validateUserCredentials(username, password);
        if (validateUserCredentials) {
            return validateUserCredentials;
        }
        return null;
    }

    @Override
    public boolean authorize(final Object arg0, final String arg1) {
        return false;
    }
}
