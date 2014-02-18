package com.swookiee.runtime.authentication.internal;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swookiee.core.configuration.ConfigurationConsumer;
import com.swookiee.runtime.authentication.AdminUserConfiguration;

@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL, configurationPid = AdminUserConfiguration.pid)
public class AdminUserManagementSerice {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserManagementSerice.class);

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private static final String USER_ADMIN_USERNAME_PROPERTY = "username";
    private static final String USER_ADMIN_PASSWORD_PROPERTY = "password";

    private ConfigurationConsumer<AdminUserConfiguration> configurationConsumer;

    private UserAdmin userAdmin;

    @Activate
    private void activate(final Map<String, String> properties) {
        this.configurationConsumer = ConfigurationConsumer.withDefaultConfiguration(getDefaultConfiguration())
                .applyConfiguration(properties);

        final AdminUserConfiguration adminUserConfiguration = configurationConsumer.getConfiguration(AdminUserConfiguration.class);

        setAdminUser(adminUserConfiguration);
    }

    @Modified
    private void modified(final Map<String, String> properties) {
        removeAdminUser();
        configurationConsumer.applyConfiguration(properties);

        final AdminUserConfiguration adminUserConfiguration = configurationConsumer.getConfiguration(AdminUserConfiguration.class);
        setAdminUser(adminUserConfiguration);
    }

    private AdminUserConfiguration getDefaultConfiguration() {
        final AdminUserConfiguration adminUserConfiguration = new AdminUserConfiguration();
        adminUserConfiguration.username = DEFAULT_ADMIN_USERNAME;
        adminUserConfiguration.password = DEFAULT_ADMIN_PASSWORD;
        return adminUserConfiguration;
    }

    @Deactivate
    private void deactivate() {
        removeAdminUser();
    }

    private void removeAdminUser() {
        final String username = configurationConsumer.getConfiguration(AdminUserConfiguration.class).username;
        this.userAdmin.removeRole(username);
    }

    private void setAdminUser(final AdminUserConfiguration configuration) {

        logger.info("Admin user has changed username: {}", configuration.username);

        final User user = createAndGetAdminUser(configuration.username);
        setPassword(user, configuration.password);

    }

    @SuppressWarnings("unchecked")
    private User createAndGetAdminUser(final String username) {

        final User existingUser = userAdmin.getUser(USER_ADMIN_USERNAME_PROPERTY, username);

        if (existingUser != null) {
            return existingUser;
        }

        final User newUser = (User) userAdmin.createRole(username, Role.USER);
        newUser.getProperties().put(USER_ADMIN_USERNAME_PROPERTY, username);

        return newUser;
    }

    @SuppressWarnings("unchecked")
    private void setPassword(final User user, final String password) {
        try {

            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] passwordHash = digest.digest(password.getBytes("UTF-8"));

            user.getCredentials().put(USER_ADMIN_PASSWORD_PROPERTY, passwordHash);

        } catch (final NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            logger.error("This should not happen " + ex.getMessage(), ex);
        }
    }

    @Reference
    public void setUserAdmin(final UserAdmin userAdmin) {
        this.userAdmin = userAdmin;
    }

    public void unsetUserAdmin(final UserAdmin userAdmin) {
        this.userAdmin = userAdmin;
    }

}
