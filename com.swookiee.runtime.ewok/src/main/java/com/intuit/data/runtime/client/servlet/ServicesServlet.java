package com.intuit.data.runtime.client.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intuit.data.runtime.client.servlet.util.ServletUtil;
import com.intuit.data.runtime.resource.representation.ServiceRepresenation;

/**
 * This {@link HttpServlet} implements the Services Resource (5.1.7) of the OSGi RFC-182 draft version 8. @see <a href
 * =https://github.com/osgi/design/tree/master/rfcs/rfc0182> https://github.com/osgi/design/tree/master/rfcs/rfc0182</a>
 * 
 */
public class ServicesServlet extends HttpServlet {

    public static final String ALIAS = "/framework/services";
    private static final String SERVICE_ID_PROPERTY = "service.id";
    private static final long serialVersionUID = 8101122973161058308L;
    private final BundleContext bundleContext;
    private static final Logger logger = LoggerFactory.getLogger(ServicesServlet.class);

    public ServicesServlet(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        final String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            ServletUtil.jsonResponse(response, getServiceList());
        } else if (pathInfo.equals("/representations")) {
            ServletUtil.jsonResponse(response, getServiceRepresenationList());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private List<String> getServiceList() {

        final List<String> services = new ArrayList<>();

        try {
            final ServiceReference<?>[] allServiceReferences = bundleContext.getAllServiceReferences(null, null);
            for (final ServiceReference<?> serviceReference : allServiceReferences) {
                final Long serviceId = (Long) serviceReference.getProperty(SERVICE_ID_PROPERTY);
                services.add(String.format("%s/%d", ServiceServlet.ALIAS, serviceId));
            }

        } catch (final InvalidSyntaxException ignored) {
            logger.error("This will never happen!", ignored);
        }

        return services;
    }

    private List<ServiceRepresenation> getServiceRepresenationList() {

        final List<ServiceRepresenation> services = new ArrayList<>();

        try {
            final ServiceReference<?>[] allServiceReferences = bundleContext.getAllServiceReferences(null, null);
            for (final ServiceReference<?> serviceReference : allServiceReferences) {

                final ServiceRepresenation represenation = new ServiceRepresenation();
                represenation.setBundle(serviceReference.getBundle().getSymbolicName());

                ServletUtil.addUsingBundles(serviceReference, represenation);

                ServletUtil.addServiceProperties(serviceReference, represenation);

                services.add(represenation);
            }

        } catch (final InvalidSyntaxException ignored) {
            logger.error("This will never happen!", ignored);
        }

        return services;
    }
}
