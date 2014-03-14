package com.swookiee.runtime.security.oauth2.token;

import org.apache.commons.lang3.RandomStringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.swookiee.runtime.security.oauth2.RefreshToken;
import com.swookiee.runtime.security.oauth2.RefreshTokenRepository;

@Component(service = RefreshTokenStorage.class)
public class RefreshTokenStorage {

    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    // TODO: global conf
    private static final int AUTH_CODE_LENGTH = 16;
    private static final long ONE_YEAR = 31557600000l;

    private RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(String clientId, String userId) {

        String refreshToken = generateRefreshToken();

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setClientId(clientId);
        refreshTokenEntity.setUserId(userId);
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpirationDate(now() + ONE_YEAR);

        refreshTokenRepository.add(refreshTokenEntity);

        return refreshToken;
    }

    public RefreshToken getRefreshTokenInfo(String refreshToken) {
        return refreshTokenRepository.get(refreshToken);
    }

    public void remove(String refreshToken) {
        refreshTokenRepository.remove(refreshToken);
    }

    @Reference
    public void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void unsetRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = null;
    }

    private String generateRefreshToken() {
        return RandomStringUtils.random(AUTH_CODE_LENGTH, ALPHABET);
    }

    private long now() {
        return System.currentTimeMillis();
    }

}
