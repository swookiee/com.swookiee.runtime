package com.swookiee.runtime.security.oauth2.token;

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
import com.swookiee.runtime.security.oauth2.servlet.AbstractOAuthServlet;
import com.swookiee.runtime.security.oauth2.servlet.GrantType;
import com.swookiee.runtime.security.oauth2.servlet.OAuthErrorCode;
import com.swookiee.runtime.security.oauth2.servlet.OAuthRequestParameters;
import com.swookiee.runtime.security.oauth2.servlet.UserService;

@Component
public class TokenServlet extends AbstractOAuthServlet {

    private static final String ALIAS_TOKEN = "/token";
    private static final Logger logger = LoggerFactory.getLogger(TokenServlet.class);

    private HttpService httpService;
    private TokenHandler tokenHandler;
    private UserService userService;

    public void activate(ComponentContext componentContext) {
        try {
            httpService.registerServlet(ALIAS_TOKEN, this, null, null);
        } catch (ServletException | NamespaceException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void deactivate(ComponentContext componentContext) {
        httpService.unregister(ALIAS_TOKEN);
    }

    @Reference
    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
    IOException {

        OAuthRequestParameters parameters = OAuthRequestParameters.parse(request);

        String clientId = parameters.getClientId();
        GrantType grantType = GrantType.get(parameters.getGrantType());

        if (clientId == null || grantType == null) {
            sendOAuthErrorResponse(response, OAuthErrorCode.INVALID_REQUEST,
                    "Request parameters 'client_id' and 'grant_type' must not be null.");
            return;
        }

        OAuthToken token = null;

        try {
            switch (grantType) {

            case AUTHORIZATION_CODE:
                String code = parameters.getCode();
                token = tokenHandler.exchangeAuthCode(clientId, code);
                break;

            case REFRESH_TOKEN:
                String refreshToken = parameters.getRefreshToken();
                token = tokenHandler.exchangeRefreshToken(clientId, refreshToken);
                break;

            case PASSWORD:

                String username = parameters.getUsername();
                String password = parameters.getPassword();

                if (userService.isValidCredentials(username, password)) {
                    token = tokenHandler.create(clientId, username);
                }

                break;

            default:
                break;
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
