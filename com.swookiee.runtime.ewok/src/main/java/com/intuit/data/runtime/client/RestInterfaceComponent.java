package com.intuit.data.runtime.client;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intuit.data.runtime.client.auth.BasicAuthHttpContext;
import com.intuit.data.runtime.client.servlet.BundleServlet;
import com.intuit.data.runtime.client.servlet.BundlesRepresentationsServlet;
import com.intuit.data.runtime.client.servlet.BundlesServlet;
import com.intuit.data.runtime.client.servlet.FrameworkStartLevelServlet;
import com.intuit.data.runtime.client.servlet.ServiceServlet;
import com.intuit.data.runtime.client.servlet.ServicesServlet;

/**
 * This class is the main component registering RFC-182 endpoints. @see <a
 * href=https://github.com/osgi/design/tree/master/rfcs/rfc0182>
 * https://github.com/osgi/design/tree/master/rfcs/rfc0182</a>
 */
@Component
public class RestInterfaceComponent {

    private final Map<String, HttpServlet> servlets = new HashMap<>();

    private BundleContext bundleContext;
    private HttpService httpService;

    private static final Logger logger = LoggerFactory.getLogger(RestInterfaceComponent.class);

    @Reference
    public void setHttpService(final HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsetHttpService(final HttpService httpService) {
        this.httpService = null;
    }

    public void activate(final ComponentContext context) {
        bundleContext = context.getBundleContext();
        registerServletsToHttpService();
        logger.info("RestInterfaceComponent activated!");
    }

    public void deactivate(final ComponentContext context) {
        unregisterServlets();
        logger.info("RestInterfaceComponent deactivated!");
    }

    private void unregisterServlets() {
        for (final String alias : servlets.keySet()) {
            httpService.unregister(alias);
        }
    }

    private void registerServletsToHttpService() {

        servlets.put(FrameworkStartLevelServlet.ALIAS, new FrameworkStartLevelServlet(this.bundleContext));
        servlets.put(BundlesServlet.ALIAS, new BundlesServlet(this.bundleContext));
        servlets.put(BundlesRepresentationsServlet.ALIAS, new BundlesRepresentationsServlet(this.bundleContext));
        servlets.put(BundleServlet.ALIAS, new BundleServlet(this.bundleContext));
        servlets.put(ServicesServlet.ALIAS, new ServicesServlet(this.bundleContext));
        servlets.put(ServiceServlet.ALIAS, new ServiceServlet(this.bundleContext));

        final BasicAuthHttpContext basicAuthHttpContext = new BasicAuthHttpContext();

        try {
            for (final String alias : servlets.keySet()) {
                httpService.registerServlet(alias, servlets.get(alias), null, basicAuthHttpContext);
            }

        } catch (ServletException | NamespaceException ex) {
            logger.error("Servlet could not be registred.", ex);
        }
    }
}
