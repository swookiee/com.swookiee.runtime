package com.swookiee.runtime.security.oauth2.token;



public class RefreshToken {


    private String clientId;


    private long expirationDate;


    private String token;

    private String userId;

    public String getClientId() {
        return clientId;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isExpired(long dateInMilliseconds) {
        return expirationDate < dateInMilliseconds;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
