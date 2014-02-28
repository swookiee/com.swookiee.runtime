package com.swookiee.runtime.security.oauth2.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swookiee.runtime.security.oauth2.UserService;
import com.swookiee.runtime.security.oauth2.authcode.AuthCodeStorage;
import com.swookiee.runtime.security.oauth2.servlet.helper.OAuthErrorCode;
import com.swookiee.runtime.security.oauth2.servlet.helper.OAuthRequestParameters;
import com.swookiee.runtime.security.oauth2.servlet.helper.ServletUtil;

@Component
public class LoginServlet extends AbstractOAuthServlet {

    public static final String ALIAS_LOGIN = "/oauth2/login";

    private AuthCodeStorage authCodeStorage;

    private HttpService httpService;
    private final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private UserService userService;

    public void activate(ComponentContext componentContext) {
        try {
            httpService.registerServlet(ALIAS_LOGIN, this, null, null);
        } catch (ServletException | NamespaceException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void deactivate(ComponentContext componentContext) {
        httpService.unregister(ALIAS_LOGIN);
    }

    @Reference
    public void setAuthCodeStorage(AuthCodeStorage authCodeStorage) {
        this.authCodeStorage = authCodeStorage;
    }

    @Reference
    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    @Reference
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void unsetAuthCodeStorage(AuthCodeStorage authCodeStorage) {
        this.authCodeStorage = authCodeStorage;
    }

    public void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    public void unsetUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        String clientId = (String) session.getAttribute(OAuthRequestParameters.CLIENT_ID);
        String redirectUri = (String) session.getAttribute(OAuthRequestParameters.REDIRECT_URI);

        if (clientId == null || redirectUri == null) {
            sendErrorRedirect(response, redirectUri, OAuthErrorCode.INVALID_REQUEST);
            return;
        }

        ServletError servletError = (ServletError) request.getSession().getAttribute(SESSION_ATTRIBUTE_SERVLET_ERROR);
        ServletUtil.writeHtmlToOutput(response, "/res/login.html", servletError);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
    IOException {

        HttpSession session = request.getSession();

        String clientId = (String) session.getAttribute(OAuthRequestParameters.CLIENT_ID);
        String redirectUri = (String) session.getAttribute(OAuthRequestParameters.REDIRECT_URI);

        if (clientId == null || redirectUri == null) {
            sendErrorRedirect(response, redirectUri, OAuthErrorCode.INVALID_REQUEST);
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (userService.isValidCredentials(username, password)) {
            String authCode = authCodeStorage.createAuthCode(clientId, username);
            sendAuthCodeRedirect(request, response, authCode, redirectUri);
        }
        else {
            logger.warn("Login for user '{}' failed", username);
            sendLoginRedirect(request, response, LoginError.INVALID_CREDENTIALS);
        }

    }
}