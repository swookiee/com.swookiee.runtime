package com.swookiee.runtime.security.oauth2.defaultimpl;

import org.osgi.service.component.annotations.Component;

import com.swookiee.runtime.security.oauth2.UserService;

@Component(service = UserService.class)
public class DefaultUserService implements UserService {

    @Override
    public boolean isValidCredentials(String username, String password) {
        if ("admin".equals(username) && "admin".equals(password)) {
            return true;
        } else {
            return false;
        }
    }

}
