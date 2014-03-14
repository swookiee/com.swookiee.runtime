package com.swookiee.runtime.security.oauth2;


public interface RefreshTokenRepository {

    void add(RefreshToken refreshToken);

    RefreshToken get(String refreshToken);

    void remove(String refreshToken);

}
