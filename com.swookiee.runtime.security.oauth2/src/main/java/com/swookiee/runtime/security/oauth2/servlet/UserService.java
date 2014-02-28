package com.swookiee.runtime.security.oauth2.servlet;

import org.osgi.service.component.annotations.Component;

@Component(service = UserService.class)
public class UserService {

    public boolean isValidCredentials(String username, String password) {
        return true;
    }

}
