package com.swookiee.runtime.security.oauth2.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.swookiee.runtime.security.oauth2.UserService;
import com.swookiee.runtime.security.oauth2.client.ClientRegistry;
import com.swookiee.runtime.security.oauth2.servlet.helper.GrantType;
import com.swookiee.runtime.security.oauth2.servlet.helper.OAuthErrorCode;
import com.swookiee.runtime.security.oauth2.token.AuthenticationException;
import com.swookiee.runtime.security.oauth2.token.OAuthToken;
import com.swookiee.runtime.security.oauth2.token.TokenCreationException;
import com.swookiee.runtime.security.oauth2.token.TokenHandler;

@Component
public class TokenServlet extends AbstractOAuthServlet {

    private static final String ALIAS_TOKEN = "/token";
    private static final Logger logger = LoggerFactory.getLogger(TokenServlet.class);
    private static final String PARAMETER_CODE = "code";
    private static final String PARAMETER_GRANT_TYPE = "grant_type";
    private static final String PARAMETER_REFRESH_TOKEN = "refresh_token";

    private ClientRegistry clientRegistry;
    private HttpService httpService;
    private TokenHandler tokenHandler;
    private UserService userService;

    @Reference
    public void setClientRegistry(ClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
    }

    @Reference
    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    @Reference
    public void setTokenHandler(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    @Reference
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void unsetClientRegistry(ClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
    }

    public void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    public void unsetTokenHandler(TokenHandler tokenHandler) {
        this.tokenHandler = null;
    }

    public void unsetUserService(UserService userService) {
        this.userService = userService;
    }

    protected void activate(ComponentContext componentContext) {
        try {
            httpService.registerServlet(ALIAS_TOKEN, this, null, null);
        } catch (ServletException | NamespaceException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    protected void deactivate(ComponentContext componentContext) {
        httpService.unregister(ALIAS_TOKEN);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
    IOException {

        String clientId = request.getParameter(PARAMETER_CLIENT_ID);
        String clientSecret = request.getParameter(PARAMETER_CLIENT_SECRET);
        String grantType = request.getParameter(PARAMETER_GRANT_TYPE);

        if (clientId == null || grantType == null) {
            sendOAuthErrorResponse(response, OAuthErrorCode.INVALID_REQUEST,
                    "Request parameters 'client_id' and 'grant_type' must not be null.");
            return;
        }

        if (!clientRegistry.isValidClientIdAndClientSecret(clientId, clientSecret)) {
            sendOAuthErrorResponse(response, OAuthErrorCode.INVALID_CLIENT,
                    "Client id does not exists or client secret is invalid.");
            return;
        }

        OAuthToken token = null;

        try {
            switch (GrantType.valueOf(grantType.toUpperCase())) {

            case AUTHORIZATION_CODE:

                String code = request.getParameter(PARAMETER_CODE);
                if (code == null) {
                    sendOAuthErrorResponse(response, OAuthErrorCode.INVALID_REQUEST,
                            "Auth code was not specified for grant type 'auth'.");
                    return;
                }
                token = tokenHandler.exchangeAuthCode(clientId, code);

                break;

            case REFRESH_TOKEN:
                String refreshToken = request.getParameter(PARAMETER_REFRESH_TOKEN);

                if (refreshToken == null) {
                    sendOAuthErrorResponse(response, OAuthErrorCode.INVALID_REQUEST,
                            "Refresh token was not specified for grant type 'refresh_token'.");
                    return;
                }

                token = tokenHandler.exchangeRefreshToken(clientId, refreshToken);

                break;

            case PASSWORD:

                String username = request.getParameter(PARAMETER_USERNAME);
                String password = request.getParameter(PARAMETER_PASSWORD);

                if (username == null || password == null) {
                    sendOAuthErrorResponse(response, OAuthErrorCode.INVALID_REQUEST,
                            "Username or password was not specified for grant type 'authorization_code'.");
                    return;
                }

                if (userService.isValidCredentials(username, password)) {
                    token = tokenHandler.create(clientId, username);
                }

                break;

            default:
                sendOAuthErrorResponse(response, OAuthErrorCode.INVALID_GRANT, "Invalid grant type: " + grantType);
                return;

            }

            response.getWriter().write(new Gson().toJson(token));
        }

        catch (AuthenticationException ex) {
            sendOAuthErrorResponse(response, OAuthErrorCode.INVALID_CLIENT, ex.getMessage());
        } catch (TokenCreationException ex) {
            sendOAuthErrorResponse(response, OAuthErrorCode.SERVER_ERROR, ex.getMessage());
        }
    }

}
