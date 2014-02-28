package com.swookiee.runtime.security.oauth2.token;

import org.apache.commons.lang3.RandomStringUtils;
import org.osgi.service.component.annotations.Component;


@Component(service = RefreshTokenStorage.class)
public class RefreshTokenStorage {

    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            .toCharArray();

    // TODO: global conf
    private static final int AUTH_CODE_LENGTH = 16;
    private static final long ONE_YEAR = 31557600000l;


    public String createRefreshToken(String clientId, String userId) {

        String refreshToken = generateRefreshToken();

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setClientId(clientId);
        refreshTokenEntity.setUserId(userId);
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpirationDate(now() + ONE_YEAR);
        // TODO: save token

        return refreshToken;
    }

    public RefreshToken getRefreshTokenInfo(String refreshToken) {
        // TODO: return refresh token
        return null;
    }

    public void remove(String refreshToken) {
        // TODO: remove token
    }

    private String generateRefreshToken() {
        return RandomStringUtils.random(AUTH_CODE_LENGTH, ALPHABET);
    }

    private long now() {
        return System.currentTimeMillis();
    }

}
