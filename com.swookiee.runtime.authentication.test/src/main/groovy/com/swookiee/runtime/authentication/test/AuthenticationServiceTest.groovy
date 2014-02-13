package com.swookiee.runtime.authentication.test

import static org.junit.Assert.*

import org.junit.Test
import org.osgi.framework.BundleContext
import org.osgi.service.useradmin.UserAdmin

import com.github.groovyosgi.testing.OSGiTest
import com.swookiee.runtime.authentication.AuthenticationService
import com.swookiee.runtime.authentication.internal.Activator

class AuthenticationServiceTest extends OSGiTest{

    @Test
    public void test() {
        UserAdmin useradmin = getService(UserAdmin)

        AuthenticationService authenticationService = getService(AuthenticationService)

        assertTrue authenticationService.validateUserCredentials("admin", "admin123")
        assertFalse authenticationService.validateUserCredentials("admin1", "test")
        assertFalse authenticationService.validateUserCredentials("admin", "test1")
    }

    @Override
    protected BundleContext getBundleContext() {
        return Activator.context
    }
}
