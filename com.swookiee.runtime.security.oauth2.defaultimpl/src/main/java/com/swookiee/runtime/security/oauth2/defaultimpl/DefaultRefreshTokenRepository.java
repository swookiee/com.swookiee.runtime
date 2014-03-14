package com.swookiee.runtime.security.oauth2.defaultimpl;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Maps;
import com.swookiee.runtime.security.oauth2.RefreshToken;
import com.swookiee.runtime.security.oauth2.RefreshTokenRepository;

@Component(service = RefreshTokenRepository.class)
public class DefaultRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<String, RefreshToken> refreshTokens = Maps.newConcurrentMap();

    @Override
    public void add(RefreshToken refreshToken) {
        refreshTokens.put(refreshToken.getToken(), refreshToken);
    }

    @Override
    public RefreshToken get(String refreshToken) {
        return refreshTokens.get(refreshToken);
    }

    @Override
    public void remove(String refreshToken) {
        refreshTokens.remove(refreshToken);
    }

}
