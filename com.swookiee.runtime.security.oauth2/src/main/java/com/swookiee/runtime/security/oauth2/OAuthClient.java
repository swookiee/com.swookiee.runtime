package com.swookiee.runtime.security.oauth2;

import java.util.List;

public class OAuthClient {

    private final String id;
    private final List<String> redirectUris;
    private final String secret;

    public OAuthClient(String id, List<String> redirectUris) {
        this.id = id;
        this.secret = null;
        this.redirectUris = redirectUris;
    }

    public OAuthClient(String id, String secret, List<String> redirectUris) {
        this.id = id;
        this.secret = secret;
        this.redirectUris = redirectUris;
    }

    public String getId() {
        return id;
    }

    public List<String> getRedirectUris() {
        return redirectUris;
    }

    public String getSecret() {
        return secret;
    }

}
