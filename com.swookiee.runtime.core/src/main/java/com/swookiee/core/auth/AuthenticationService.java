package com.swookiee.core.auth;

public interface AuthenticationService {

    boolean validateUserCredentials(String username, String password);

}
