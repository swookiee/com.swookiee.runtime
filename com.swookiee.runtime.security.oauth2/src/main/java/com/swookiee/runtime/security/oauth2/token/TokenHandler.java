package com.swookiee.runtime.security.oauth2.token;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.swookiee.runtime.security.oauth2.authcode.AuthCodeInfo;
import com.swookiee.runtime.security.oauth2.authcode.AuthCodeStorage;
import com.swookiee.runtime.security.oauth2.client.ClientRegistry;
import com.swookiee.runtime.security.oauth2.token.AuthenticationException.AuthenticationError;

@Component(service = TokenHandler.class)
public class TokenHandler {


    private AuthCodeStorage authCodeStorage;

    private ClientRegistry clientRegistry;

    private RefreshTokenStorage refreshTokenStorage;
    public OAuthToken create(String clientId, String userId) throws TokenCreationException, AuthenticationException {

        if (!clientRegistry.clientExists(clientId)) {
            throw new AuthenticationException(AuthenticationError.CLIENT_NOT_FOUND, clientId);
        }

        return createToken(clientId, userId);
    }

    public OAuthToken exchangeAuthCode(String clientId, String authCode) throws AuthenticationException,
    TokenCreationException {

        if (!clientRegistry.clientExists(clientId)) {
            throw new AuthenticationException(AuthenticationError.CLIENT_NOT_FOUND, clientId);
        }

        AuthCodeInfo authCodeInfo = authCodeStorage.getAuthCodeInfo(authCode);

        if (authCodeInfo == null) {
            throw new AuthenticationException(AuthenticationError.AUTH_CODE_NOT_FOUND, authCode);
        }

        if (authCodeInfo.isExpired(now())) {
            throw new AuthenticationException(AuthenticationError.AUTH_CODE_EXPIRED, authCode);
        }

        String authCodeClientId = authCodeInfo.getClientId();

        if (!authCodeClientId.equals(clientId)) {
            throw new AuthenticationException(AuthenticationError.AUTH_CODE_DIFFERENT_CLIENT_ID, authCode,
                    authCodeClientId, clientId);
        }

        OAuthToken token = createToken(clientId, authCodeInfo.getUserId());

        authCodeStorage.remove(authCode);

        return token;
    }
    public OAuthToken exchangeRefreshToken(String clientId, String refreshToken) throws AuthenticationException,
    TokenCreationException {

        if (!clientRegistry.clientExists(clientId)) {
            throw new AuthenticationException(AuthenticationError.CLIENT_NOT_FOUND, clientId);
        }

        RefreshToken refreshTokenEntity = refreshTokenStorage.getRefreshTokenInfo(refreshToken);

        if (refreshTokenEntity == null) {
            throw new AuthenticationException(AuthenticationError.REFRESH_TOKEN_NOT_FOUND, refreshToken);
        }

        if (refreshTokenEntity.isExpired(now())) {
            throw new AuthenticationException(AuthenticationError.REFRESH_TOKEN_EXPIRED, refreshToken);
        }

        String refreshTokenClientId = refreshTokenEntity.getClientId();

        if (!refreshTokenClientId.equals(clientId)) {
            throw new AuthenticationException(AuthenticationError.AUTH_CODE_DIFFERENT_CLIENT_ID, refreshToken,
                    refreshTokenClientId, clientId);
        }

        OAuthToken token = createToken(clientId, refreshTokenEntity.getUserId(), false);

        // TODO: clean up expired entries

        return token;
    }

    @Reference
    public void setAuthCodeStorage(AuthCodeStorage authCodeStorage) {
        this.authCodeStorage = authCodeStorage;
    }

    @Reference
    public void setClientRegistry(ClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
    }

    @Reference
    public void setRefreshTokenStorage(RefreshTokenStorage refreshTokenStorage) {
        this.refreshTokenStorage = refreshTokenStorage;
    }

    public void unsetAuthCodeStorage(AuthCodeStorage authCodeStorage) {
        this.authCodeStorage = null;
    }

    public void unsetClientRegistry(ClientRegistry clientRegistry) {
        this.clientRegistry = null;
    }

    public void unsetRefreshTokenStorage(RefreshTokenStorage refreshTokenStorage) {
        this.refreshTokenStorage = null;
    }

    private OAuthToken createToken(String clientId, String userId) throws TokenCreationException {
        return createToken(clientId, userId, true);
    }

    private OAuthToken createToken(String clientId, String userId, boolean createRefreshToken)
            throws TokenCreationException {

        String accessToken = new AccessTokenGenerator().generate(userId);
        String refreshToken = null;

        if (createRefreshToken) {
            refreshToken = refreshTokenStorage.createRefreshToken(clientId, userId);
        }

        int expiresIn = 1; // TODO: define expires in

        return new OAuthToken(accessToken, refreshToken, expiresIn);
    }

    private long now() {
        return System.currentTimeMillis();
    }

}
