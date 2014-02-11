package com.swookiee.runtime.ewok.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swookiee.runtime.ewok.representation.FrameworkStartLevelRepresentation;
import com.swookiee.runtime.ewok.util.HttpErrorException;
import com.swookiee.runtime.ewok.util.ServletUtil;

/**
 * This {@link HttpServlet} implements the Framework Startlevel Resource (5.1.1) of the OSGi RFC-182 draft version 8. @see
 * <a href =https://github.com/osgi/design/tree/master/rfcs/rfc0182>
 * https://github.com/osgi/design/tree/master/rfcs/rfc0182</a>
 * 
 */
public class FrameworkStartLevelServlet extends HttpServlet {

    public static final String ALIAS = "/framework/startlevel";

    private static final long SYSTEM_BUNDLE_ID = 0;
    private static final long serialVersionUID = 7499406060780395457L;
    private final FrameworkStartLevel frameworkStartLevel;
    private static final Logger logger = LoggerFactory.getLogger(FrameworkStartLevelServlet.class);
    private final ObjectMapper mapper;

    public FrameworkStartLevelServlet(final BundleContext bundleContext) {
        this.frameworkStartLevel = bundleContext.getBundle(SYSTEM_BUNDLE_ID).adapt(FrameworkStartLevel.class);
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        final FrameworkStartLevelRepresentation frameworkStartlevelRepresentation = new FrameworkStartLevelRepresentation(
                getFrameworkStartLevel(), getInitialBundleStartLevel());

        ServletUtil.jsonResponse(response, frameworkStartlevelRepresentation);
    }

    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        try {
            ServletUtil.checkForJsonMediaType(request);

            final FrameworkStartLevelRepresentation startLevelRepresentation = mapper.readValue(request.getReader(),
                    FrameworkStartLevelRepresentation.class);

            changeFrameworkStartLevel(startLevelRepresentation);

            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (final HttpErrorException ex) {
            response.sendError(ex.getHttpErrorCode());
        } catch (final Exception ex) {
            logger.error("Error while changing FrameworkStartLevel ", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void changeFrameworkStartLevel(final FrameworkStartLevelRepresentation startLevelRepresentation) {
        final int initialStartLevel = startLevelRepresentation.getInitialStartLevel();
        final int startLevel = startLevelRepresentation.getStartLevel();

        if (getInitialBundleStartLevel() != initialStartLevel) {
            frameworkStartLevel.setInitialBundleStartLevel(initialStartLevel);
        }
        if (getFrameworkStartLevel() != startLevel) {
            frameworkStartLevel.setStartLevel(startLevel);
        }
    }

    private int getFrameworkStartLevel() {
        return frameworkStartLevel.getStartLevel();
    }

    private int getInitialBundleStartLevel() {
        return frameworkStartLevel.getInitialBundleStartLevel();
    }

}
