package com.swookiee.runtime.security.oauth2;

public interface UserService {

    boolean isValidCredentials(String username, String password);

}
