package com.swookiee.runtime.security.oauth2.authcode;

import com.swookiee.runtime.security.oauth2.util.SecureRandomInfo;

public class AuthCodeInfo extends SecureRandomInfo {

    public AuthCodeInfo(String clientId, String userId, long expirationDate) {
        super(clientId, userId, expirationDate);
    }
}