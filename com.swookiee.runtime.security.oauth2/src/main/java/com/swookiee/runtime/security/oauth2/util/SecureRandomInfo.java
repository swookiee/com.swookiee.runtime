package com.swookiee.runtime.security.oauth2.util;

public class SecureRandomInfo {
    private final String clientId;

    private final long expirationDate;
    private final String userId;

    public SecureRandomInfo(String clientId, String userId, long expirationDate) {
        this.clientId = clientId;
        this.userId = userId;
        this.expirationDate = expirationDate;
    }

    public String getClientId() {
        return clientId;
    }

    public long getExpirationDate() {
        return expirationDate;
    }
    public String getUserId() {
        return userId;
    }

    public boolean isExpired(long dateInMilliseconds) {
        return expirationDate < dateInMilliseconds;
    }

}
