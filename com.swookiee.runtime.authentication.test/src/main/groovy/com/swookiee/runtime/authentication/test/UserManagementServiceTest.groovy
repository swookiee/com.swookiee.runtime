package com.swookiee.runtime.authentication.test

import static org.junit.Assert.*

import org.junit.After
import org.junit.Test
import org.osgi.framework.BundleContext
import org.osgi.service.cm.Configuration
import org.osgi.service.cm.ConfigurationAdmin

import com.github.groovyosgi.testing.OSGiTest
import com.swookiee.runtime.authentication.AuthenticationService
import com.swookiee.runtime.authentication.internal.Activator

class UserManagementServiceTest extends OSGiTest {

    @After
    public void resetDefaults(){

        Dictionary<String, Object> config = new Hashtable<>()
        config.put("user.admin.username", "admin")
        config.put("user.admin.password", "admin123")

        configuration.update(config)
        sleep(200)
    }

    @Test
    public void 'Change username and password'() {

        Dictionary<String, Object> config = new Hashtable<>()
        config.put("user.admin.username", "test")
        config.put("user.admin.password", "testtest")

        configuration.update(config)
        sleep(200)

        AuthenticationService authenticationService = getService(AuthenticationService)

        assertFalse authenticationService.validateUserCredentials("admin", "admin123")
        assertTrue authenticationService.validateUserCredentials("test", "testtest")
    }

    @Test
    public void 'Change password'() {

        Dictionary<String, Object> config = new Hashtable<>()
        config.put("user.admin.password", "testtest")

        configuration.update(config)
        sleep(200)

        AuthenticationService authenticationService = getService(AuthenticationService)

        assertFalse authenticationService.validateUserCredentials("admin", "admin123")
        assertTrue authenticationService.validateUserCredentials("admin", "testtest")
    }

    @Test
    public void 'Change username'() {

        Dictionary<String, Object> config = new Hashtable<>()
        config.put("user.admin.username", "newAdmin")

        configuration.update(config)
        sleep(200)

        AuthenticationService authenticationService = getService(AuthenticationService)

        assertFalse authenticationService.validateUserCredentials("admin", "admin123")
        assertTrue authenticationService.validateUserCredentials("newAdmin", "admin123")
    }

    def Configuration getConfiguration(){
        ConfigurationAdmin configurationAdmin = getService(ConfigurationAdmin)
        Configuration configuration = configurationAdmin.getConfiguration("com.swookiee.runtime.authentication.internal.UserManagementService")
    }

    @Override
    protected BundleContext getBundleContext() {
        return Activator.context
    }
}
