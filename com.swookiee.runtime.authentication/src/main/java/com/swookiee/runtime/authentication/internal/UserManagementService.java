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

@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class UserManagementService {


    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String USER_ADMIN_USERNAME_PROPERTY = "username";
    private static final String USER_ADMIN_PASSWORD_PROPERTY = "password";
    private static final String CONFIGURATION_PASSWORD_PROPERTY = "user.admin.password";
    private static final String CONFIGURATION_USERNAME_PROPERTY = "user.admin.username";

    private String lastUsername;
    private String lastPassword;

    private UserAdmin userAdmin;

    @Activate
    private void activate(final Map<String, String> properties) {

        if (properties.containsKey(CONFIGURATION_USERNAME_PROPERTY)
                && properties.containsKey(CONFIGURATION_PASSWORD_PROPERTY)) {
            setAdminUserFromConfiguration(properties);
        } else {
            setAdminUser(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD);
        }
    }

    @Modified
    private void modified(final Map<String, String> properties) {

        if (properties.containsKey(CONFIGURATION_USERNAME_PROPERTY)
                && properties.containsKey(CONFIGURATION_PASSWORD_PROPERTY)) {
            this.userAdmin.removeRole(lastUsername);
            setAdminUserFromConfiguration(properties);
        } else if (properties.containsKey(CONFIGURATION_PASSWORD_PROPERTY)) {
            setAdminUser(lastUsername, properties.get(CONFIGURATION_PASSWORD_PROPERTY));
        } else if (properties.containsKey(CONFIGURATION_USERNAME_PROPERTY)) {
            this.userAdmin.removeRole(lastUsername);
            setAdminUser(properties.get(CONFIGURATION_USERNAME_PROPERTY), lastPassword);
        }
    }

    @Deactivate
    private void deactivate() {
        this.userAdmin.removeRole(lastUsername);
    }

    private void setAdminUserFromConfiguration(final Map<String, String> properties) {
        final String username = properties.get(CONFIGURATION_USERNAME_PROPERTY);
        final String password = properties.get(CONFIGURATION_PASSWORD_PROPERTY);

        setAdminUser(username, password);
    }

    private void setAdminUser(final String username, final String password) {
        logger.info("Admin user has changed username: {}", username);
        final User user = createAndGetAdminUser(username);
        setPassword(user, password);
        this.lastUsername = username;
        this.lastPassword = password;
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
