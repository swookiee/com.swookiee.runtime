package com.swookiee.runtime.ewok.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swookiee.runtime.ewok.representation.ServiceRepresenation;
import com.swookiee.runtime.ewok.util.HttpErrorException;
import com.swookiee.runtime.ewok.util.ServletUtil;

/**
 * This {@link HttpServlet} implements the Service Resource (5.1.8) of the OSGi RFC-182 draft version 8. @see <a href=
 * https://github.com/osgi/design/tree/master/rfcs/rfc0182> https://github.com/osgi/design/tree/master/rfcs/rfc0182</a>
 * 
 */
public class ServiceServlet extends HttpServlet {

    public static final String ALIAS = "/framework/service";
    private static final long serialVersionUID = -8784847583201231060L;
    private static final Logger logger = LoggerFactory.getLogger(ServiceServlet.class);
    private final BundleContext bundleContext;
    private final ObjectMapper mapper;

    public ServiceServlet(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {

            final long serviceId = ServletUtil.getId(request);
            String json = null;

            json = getServiceRepresentation(serviceId);

            response.setContentType("application/json");
            response.getWriter().println(json);

        } catch (final HttpErrorException ex) {
            logger.warn(ex.getMessage());
            ServletUtil.errorResponse(response, ex);
        }
    }

    private String getServiceRepresentation(final long serviceId) throws HttpErrorException {
        final String filter = String.format("(service.id=%d)", serviceId);

        ServiceReference<?>[] services;
        try {
            services = bundleContext.getServiceReferences((String) null, filter);

            if (services == null) {
                throw new HttpErrorException(String.format("No Service with ID '%s' Found", serviceId),
                        HttpServletResponse.SC_NOT_FOUND);
            } else if (services.length > 1 || services.length < 0) {
                throw new HttpErrorException("Illegal State", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            final ServiceReference<?> serviceReference = services[0];

            final ServiceRepresenation serviceRepresenation = new ServiceRepresenation();

            serviceRepresenation.setBundle(serviceReference.getBundle().getSymbolicName());
            ServletUtil.addUsingBundles(serviceReference, serviceRepresenation);
            ServletUtil.addServiceProperties(serviceReference, serviceRepresenation);

            return mapper.writeValueAsString(serviceRepresenation);

        } catch (final InvalidSyntaxException | JsonProcessingException ex) {
            logger.error("Cannot handle request " + ex.getMessage(), ex);
            throw new HttpErrorException("Cannot handle request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
