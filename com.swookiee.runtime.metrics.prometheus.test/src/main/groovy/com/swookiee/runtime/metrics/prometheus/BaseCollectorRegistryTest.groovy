package com.swookiee.runtime.metrics.prometheus

import io.prometheus.client.CollectorRegistry

import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration

abstract class BaseCollectorRegistryTest {

    def CollectorRegistry collectorRegistry = [] as CollectorRegistry

    def getBundleContextMock() {

        ServiceRegistration serviceRegistration = [
            unregister:{
                collectorRegistry.clear()
                void
            }
        ] as ServiceRegistration

        BundleContext bundleContext = [
            registerService: { clazz, service, properties ->
                collectorRegistry.register service
                serviceRegistration
            }
        ] as BundleContext
        bundleContext
    }
}
