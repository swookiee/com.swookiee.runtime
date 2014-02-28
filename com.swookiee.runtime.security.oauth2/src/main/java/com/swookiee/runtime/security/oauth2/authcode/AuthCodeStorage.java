package com.swookiee.runtime.security.oauth2.authcode;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Maps;

@Component(service = AuthCodeStorage.class)
public class AuthCodeStorage {

    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            .toCharArray();
    private static final int AUTH_CODE_LENGTH = 16;
    private static final int TEN_MINUTES = 600000 /* ms */;

    private final Map<String, AuthCodeInfo> authCodes = Maps.newConcurrentMap();


    public String createAuthCode(String clientId, String userId) {

        String authCode = generateAuthCode();
        authCodes.put(authCode, new AuthCodeInfo(clientId, userId, now() + TEN_MINUTES));

        return authCode;
    }

    public AuthCodeInfo getAuthCodeInfo(String authCode) {
        return authCode != null ? authCodes.get(authCode) : null;
    }

    public void remove(String authCode) {
        this.authCodes.remove(authCode);
    }

    private String generateAuthCode() {
        // return RandomStringUtils.random(AUTH_CODE_LENGTH, ALPHABET);
        // TODO:
        return "xyz";
    }

    private long now() {
        return System.currentTimeMillis();
    }

}
