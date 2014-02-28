package com.swookiee.runtime.security.oauth2.token;

import com.google.gson.annotations.SerializedName;


public class OAuthToken {

    @SerializedName("access_token")
    private final String accessToken;

    @SerializedName("expires_in")
    private final long expiresIn;

    @SerializedName("refresh_token")
    private final String refreshToken;

    @SerializedName("scope")
    private final String scope = "swookiee";

    @SerializedName("token_type")
    private final String tokenType = "bearer";

    public OAuthToken(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = null;
        this.expiresIn = expiresIn;
    }

    public OAuthToken(String accessToken, String refreshToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getScope() {
        return scope;
    }


    public String getTokenType() {
        return tokenType;
    }

}
