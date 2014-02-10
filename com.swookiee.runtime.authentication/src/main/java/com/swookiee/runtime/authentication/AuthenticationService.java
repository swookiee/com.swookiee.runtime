package com.swookiee.runtime.authentication;

public interface AuthenticationService {

    boolean validateUserCredentials(String username, String password);

}
