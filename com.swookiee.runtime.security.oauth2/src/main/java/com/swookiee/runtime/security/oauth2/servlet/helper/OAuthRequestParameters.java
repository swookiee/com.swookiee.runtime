package com.swookiee.runtime.security.oauth2.servlet.helper;

import javax.servlet.http.HttpServletRequest;

public final class OAuthRequestParameters {

    public static final String CLIENT_ID = "client_id";
    public static final String CODE = "code";
    public static final String GRANT_TYPE = "grant_type";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String RESPONSE_TYPE = "response_type";
    public static final String SCOPE = "scope";
    public static final String STATE = "state";
    private static final String P_FIELD = "p";
    private static final String PASSWORD = "password";
    private static final String SOCIAL_TOKEN = "social_token";
    private static final String SOCIAL_TOKEN_TYPE = "social_token_type";
    private static final String U_FIELD = "u";
    private static final String USERNAME = "username";

    public static OAuthRequestParameters parse(HttpServletRequest request) {
        return new OAuthRequestParameters(request);
    }

    private final String clientId;
    private final String code;
    private final String grantType;
    private final String p;
    private final String password;
    private final String redirectUri;
    private final String refreshToken;
    private final String responseType;
    private final String scope;
    private final String socialToken;
    private final String socialTokenType;

    private final String state;

    private final String u;

    private final String username;
    private OAuthRequestParameters(HttpServletRequest request) {
        this.responseType = request.getParameter(RESPONSE_TYPE);
        this.clientId = request.getParameter(CLIENT_ID);
        this.redirectUri = request.getParameter(REDIRECT_URI);
        this.state = request.getParameter(STATE);
        this.scope = request.getParameter(SCOPE);
        this.grantType = request.getParameter(GRANT_TYPE);
        this.code = request.getParameter(CODE);
        this.refreshToken = request.getParameter(REFRESH_TOKEN);
        this.u = request.getParameter(U_FIELD);
        this.p = request.getParameter(P_FIELD);
        this.username = request.getParameter(USERNAME);
        this.password = request.getParameter(PASSWORD);
        this.socialToken = request.getParameter(SOCIAL_TOKEN);
        this.socialTokenType = request.getParameter(SOCIAL_TOKEN_TYPE);
    }
    public String getClientId() {
        return clientId;
    }

    public String getCode() {
        return code;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getP() {
        return p;
    }

    public String getPassword() {
        return password;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getScope() {
        return scope;
    }

    public String getSocialToken() {
        return socialToken;
    }

    public String getSocialTokenType() {
        return socialTokenType;
    }

    public String getState() {
        return state;
    }

    public String getU() {
        return u;
    }

    public String getUsername() {
        return username;
    }
}
