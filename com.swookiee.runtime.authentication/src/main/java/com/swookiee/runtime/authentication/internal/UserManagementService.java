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

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private String lastUsername;

    private UserAdmin userAdmin;

    @Activate
    private void activate(final Map<String, String> properties) {

        if (properties.containsKey("user.admin.username") && properties.containsKey("user.admin.password")) {
            setAdminUserFromConfiguration(properties);
        } else {
            setAdminUser(ADMIN_USERNAME, ADMIN_PASSWORD);
        }
    }

    @Modified
    private void modified(final Map<String, String> properties) {

        if (properties.containsKey("user.admin.username") && properties.containsKey("user.admin.password")) {
            this.userAdmin.removeRole(lastUsername);
            setAdminUserFromConfiguration(properties);
        } else if (properties.containsKey("user.admin.password")) {
            setAdminUser(lastUsername, properties.get("user.admin.password"));
        }
    }

    @Deactivate
    private void deactivate() {
        this.userAdmin.removeRole(lastUsername);
    }

    private void setAdminUserFromConfiguration(final Map<String, String> properties) {
        final String username = properties.get("user.admin.username");
        final String password = properties.get("user.admin.password");

        setAdminUser(username, password);
    }

    private void setAdminUser(final String username, final String password) {
        final User user = createAndGetAdminUser(username);
        setPassword(user, password);
        lastUsername = username;
    }

    @SuppressWarnings("unchecked")
    private User createAndGetAdminUser(final String username) {

        final User existingUser = userAdmin.getUser(USERNAME_KEY, username);

        if (existingUser != null) {
            return existingUser;
        }

        final User newUser = (User) userAdmin.createRole(username, Role.USER);
        newUser.getProperties().put(USERNAME_KEY, username);

        return newUser;
    }

    @SuppressWarnings("unchecked")
    private void setPassword(final User user, final String password) {
        try {

            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] passwordHash = digest.digest(password.getBytes("UTF-8"));

            user.getCredentials().put(PASSWORD_KEY, passwordHash);

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
