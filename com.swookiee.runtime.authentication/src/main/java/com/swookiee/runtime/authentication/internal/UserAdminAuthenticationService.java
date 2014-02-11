package com.swookiee.runtime.authentication.internal;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swookiee.runtime.authentication.AuthenticationService;

@Component
public class UserAdminAuthenticationService implements AuthenticationService {

    private UserAdmin userAdmin;

    private static final Logger logger = LoggerFactory.getLogger(UserAdminAuthenticationService.class);

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    public void setUserAdmin(final UserAdmin userAdmin) {
        this.userAdmin = userAdmin;
    }

    public void unsetUserAdmin(final UserAdmin userAdmin) {
        this.userAdmin = null;
    }

    @Override
    public boolean validateUserCredentials(final String username, final String password) {
        if (userAdmin == null) {
            logger.warn("OSGi User Admin Service not available, no authentication possible");
            return false;
        }

        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] passwordHash = digest.digest(password.getBytes("UTF-8"));

            final User user = this.userAdmin.getUser("username", username);

            return (user != null && user.hasCredential("password", passwordHash));

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            logger.error(
                    "This really should not happen during user authentication, in case it does: " + ex.getMessage(), ex);
        }
        return false;
    }
}
