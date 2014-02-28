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

import com.swookiee.runtime.security.oauth2.client.ClientRegistry;

@Component
public class AuthorizeServlet extends AbstractOAuthServlet {

    private static final String ALIAS_AUTHORIZE = "/oauth2/authorize";

    private ClientRegistry clientRegistry;

    private HttpService httpService;

    private final Logger logger = LoggerFactory.getLogger(AuthorizeServlet.class);

    public void activate(ComponentContext componentContext) {
        try {
            httpService.registerServlet(ALIAS_AUTHORIZE, this, null, null);
        } catch (ServletException | NamespaceException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void deactivate(ComponentContext componentContext) {
        httpService.unregister(ALIAS_AUTHORIZE);
    }

    @Reference
    public void setClientRegistry(ClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
    }

    @Reference
    public void setHttpService(final HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsetClientRegistry(ClientRegistry clientRegistry) {
        this.clientRegistry = null;
    }

    public void unsetHttpService(final HttpService httpService) {
        this.httpService = null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        OAuthRequestParameters requestParameters = OAuthRequestParameters.parse(request);

        HttpSession session = request.getSession();

        String clientId = requestParameters.getClientId();
        String redirectUri = requestParameters.getRedirectUri();

        if (clientId == null || redirectUri == null) {
            logger.warn("Authorization of client request failed, because client id or redirect URI parameter was null.");
            sendErrorRedirect(response, redirectUri, OAuthErrorCode.INVALID_REQUEST);
            return;
        }

        if (!clientRegistry.isValid(clientId, redirectUri)) {
            logger.warn(
                    "Authorization of client request failed, because client id '{}' or redirect URI '{}' was not valid.",
                    clientId, redirectUri);
            sendErrorRedirect(response, redirectUri, OAuthErrorCode.ACCESS_DENIED);
            return;
        }

        session.setAttribute(OAuthRequestParameters.CLIENT_ID, clientId);
        session.setAttribute(OAuthRequestParameters.REDIRECT_URI, redirectUri);

        sendLoginRedirect(request, response);
    }
}