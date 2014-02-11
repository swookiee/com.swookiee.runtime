package com.swookiee.runtime.authentication.test

import static org.junit.Assert.*

import org.junit.After
import org.junit.Test
import org.osgi.framework.BundleContext
import org.osgi.service.cm.Configuration
import org.osgi.service.cm.ConfigurationAdmin

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.groovyosgi.testing.OSGiTest
import com.swookiee.runtime.authentication.AdminUserConfiguration
import com.swookiee.runtime.authentication.AuthenticationService
import com.swookiee.runtime.authentication.internal.Activator

class UserManagementServiceTest extends OSGiTest {

    @After
    public void resetDefaults(){

        AdminUserConfiguration config = new AdminUserConfiguration()
        config.username = "admin"
        config.password = "admin123"

        updateConfiguration(config)

        sleep(200)
    }

    @Test
    public void 'Change username and password'() {

        AdminUserConfiguration config = new AdminUserConfiguration()
        config.username = "test"
        config.password ="testtest"

        updateConfiguration(config)
        sleep(200)

        AuthenticationService authenticationService = getService(AuthenticationService)

        assertFalse authenticationService.validateUserCredentials("admin", "admin123")
        assertTrue authenticationService.validateUserCredentials("test", "testtest")
    }

    @Test
    public void 'Change password'() {

        AdminUserConfiguration config = new AdminUserConfiguration()
        config.password ="testtest"

        updateConfiguration(config)
        sleep(200)

        AuthenticationService authenticationService = getService(AuthenticationService)

        assertFalse authenticationService.validateUserCredentials("admin", "admin123")
        assertTrue authenticationService.validateUserCredentials("admin", "testtest")
    }

    @Test
    public void 'Change username'() {

        AdminUserConfiguration config = new AdminUserConfiguration()
        config.username ="newAdmin"

        updateConfiguration(config)
        sleep(200)

        AuthenticationService authenticationService = getService(AuthenticationService)

        assertFalse authenticationService.validateUserCredentials("admin", "admin123")
        assertTrue authenticationService.validateUserCredentials("newAdmin", "admin123")
    }

    def updateConfiguration(AdminUserConfiguration adminUserConfiguration){
        ObjectMapper mapper = new ObjectMapper()
        Hashtable<String, Object> configurationMap = mapper.convertValue(adminUserConfiguration, Hashtable)
        getConfiguration().update(configurationMap)
    }

    def Configuration getConfiguration(){
        ConfigurationAdmin configurationAdmin = getService(ConfigurationAdmin)
        Configuration configuration = configurationAdmin.getConfiguration(AdminUserConfiguration.pid)
    }

    @Override
    protected BundleContext getBundleContext() {
        return Activator.context
    }
}
