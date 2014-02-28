package com.swookiee.runtime.security.oauth2.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.swookiee.runtime.security.oauth2.servlet.helper.ErrorResponse;
import com.swookiee.runtime.security.oauth2.servlet.helper.OAuthErrorCode;

public abstract class AbstractOAuthServlet extends HttpServlet {

    public enum GeneralError implements ServletError {
        UNKNOWN;

        @Override
        public String getName() {
            return this.name();
        }
    }

    public enum LoginError implements ServletError {
        INVALID_CREDENTIALS;

        @Override
        public String getName() {
            return this.name();
        }
    }

    public interface ServletError {
        String getName();
    }

    protected static final String SESSION_ATTRIBUTE_SERVLET_ERROR = "servlet_error";

    private static final Logger logger = LoggerFactory.getLogger(AbstractOAuthServlet.class);


    protected void sendAuthCodeRedirect(final HttpServletRequest request, final HttpServletResponse response,
            final String authCode, final String redirectUri) {
        try {
            request.getSession().invalidate();
            response.sendRedirect(redirectUri + "?code=" + authCode);
        }
        catch (IOException ex) {
            logger.error("Could not send redirect: " + ex.getMessage(), ex);
        }
    }

    protected void sendErrorRedirect(final HttpServletResponse response, final String redirectUri,
            final OAuthErrorCode error) {
        try {

            logger.info(error.getError());

            if (redirectUri != null) {
                response.sendRedirect(redirectUri + "?error=" + error.getError());
            }
            else {
                response.sendError(400, error.getError());
            }
        }
        catch (IOException ex) {
            logger.error("Could not send redirect or error response: " + ex.getMessage(), ex);
        }
    }

    protected void sendErrorResponse(final HttpServletResponse response, int statusCode, String error,
            final String description) {
        try {
            response.getWriter().write(new Gson().toJson(new ErrorResponse(error, description)));
            response.sendError(statusCode);
        }
        catch (IOException ex) {
            logger.error("Could not send error response: " + ex.getMessage(), ex);
        }
    }

    protected void sendLoginRedirect(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        sendLoginRedirect(request, response, null);
    }

    protected void sendLoginRedirect(final HttpServletRequest request, final HttpServletResponse response,
            final ServletError servletError) {
        sendServletRedirect(LoginServlet.ALIAS_LOGIN, request, response, servletError);
    }

    protected void sendOAuthErrorResponse(final HttpServletResponse response, final OAuthErrorCode errorCode,
            final String description) {
        sendErrorResponse(response, 400, errorCode.getError(), description);
    }

    protected void sendServletRedirect(String servletAlias, final HttpServletRequest request,
            final HttpServletResponse response, final ServletError servletError) {

        if (servletError != null) {
            request.getSession().setAttribute(SESSION_ATTRIBUTE_SERVLET_ERROR, servletError);
        }

        try {
            response.sendRedirect(servletAlias);
        }
        catch (IOException ex) {
            String message = "Could not redirect to login: " + ex.getMessage();
            logger.error(message, ex);
            sendErrorResponse(response, 500, "INTERNAL_ERROR", message);
        }
    }
}
