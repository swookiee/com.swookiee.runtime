package com.swookiee.runtime.util.guice;

import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.spi.ProvisionListener;
import com.swookiee.runtime.util.guice.OSGiExport.ServiceProperties;

public class OSGiExportListener implements ProvisionListener {

    private static final Logger logger = LoggerFactory.getLogger(OSGiExportListener.class);
    private final BundleContext bundleContext;
    private final List<ServiceRegistration<?>> registeredServices;

    public OSGiExportListener(final BundleContext bundleContext, final List<ServiceRegistration<?>> registeredServices) {
        this.bundleContext = bundleContext;
        this.registeredServices = registeredServices;
    }

    @Override
    public <T> void onProvision(final ProvisionInvocation<T> provision) {
        final T provisionedInstance = provision.provision();
        if (!provisionedInstance.getClass().isAnnotationPresent(OSGiExport.class)) {
            return;
        }
        registerAsOSGiService(provisionedInstance);
    }

    // This could be reused if we want to use the @OSGiExport annotation together with @Provides methods.
    //
    // private <T> boolean isUsingOSGiExportProvider(final ProvisionInvocation<T> provision) {
    // final Object source = provision.getBinding().getSource();
    // if (source instanceof Method) {
    // final Annotation[] annotations = ((Method) source).getDeclaredAnnotations();
    // for (final Annotation annotation : annotations) {
    // if (annotation.getClass().getInterfaces()[0].equals(OSGiExport.class)) {
    // return true;
    // }
    // }
    // }
    // return false;
    // }

    private <T> void registerAsOSGiService(final T provisionedInstance) {
        final OSGiExport annotation = provisionedInstance.getClass().getAnnotation(OSGiExport.class);
        final Hashtable<String, String> propertiesAsTable = getServiceProperties(annotation);
        final ServiceRegistration<?> registeredService = bundleContext.registerService(provisionedInstance.getClass()
                .getName(), provisionedInstance, propertiesAsTable);

        registeredServices.add(registeredService);

        logger.debug("{} annotation found and service {} registered", OSGiExport.class.getSimpleName(),
                provisionedInstance.getClass().getName());

    }

    private Hashtable<String, String> getServiceProperties(final OSGiExport annotation) {
        final ServiceProperties[] properties = annotation.properties();
        final Hashtable<String, String> propertiesAsTable = new Hashtable<String, String>();

        if (properties != null && properties.length > 0) {
            for (final ServiceProperties serviceProperties : properties) {
                propertiesAsTable.put(serviceProperties.key(), serviceProperties.value());
            }
        }
        return propertiesAsTable;
    }
}
