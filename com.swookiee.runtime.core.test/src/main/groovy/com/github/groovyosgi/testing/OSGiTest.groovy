package com.github.groovyosgi.testing

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import org.junit.After
import org.junit.Before
import org.osgi.framework.BundleContext

abstract class OSGiTest {

    static BundleContext bundleContext
    def registeredServices = [:]

    protected abstract BundleContext getBundleContext()

    @Before
    void bindBundleContext() {
        bundleContext = getBundleContext()
        assertThat bundleContext, is(notNullValue())
    }

    def <T> T getService(Class<T> clazz){
        def serviceReference = bundleContext.getServiceReference(clazz.name)
        assertThat serviceReference, is(notNullValue())
        bundleContext.getService(serviceReference)
    }

    def registerMock(def mock, Hashtable properties = [:]) {
        def interfaceName = mock.class.interfaces?.find({it})?.name
        assertThat interfaceName, is(notNullValue())
        registeredServices.put(interfaceName, bundleContext.registerService(interfaceName, mock, properties))
    }

    def unregisterMock(def mock) {
        registeredServices.get(mock.interfaceName).unregister()
        registeredServices.remove(mock.interfaceName)
    }

    @After
    void unregisterMocks(){
        registeredServices.each() { interfaceName, service ->
            service.unregister()
        }
        registeredServices.clear()
    }
}
