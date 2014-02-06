

package com.intuit.data.runtime.client

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.junit.Test
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceReference

import com.intuit.data.runtime.client.servlet.ServiceServlet

public class ServiceServletTest {

    @Test
    void 'call GET and return service representation'() {

        BundleContext bundleContext = [getServiceReferences:{ def a, def b ->
                [
                    [getBundle : {
                            [getSymbolicName:{"com.test.bundle"}] as Bundle
                        }
                        , getUsingBundles:{null},
                        getPropertyKeys:{
                            ["Foo", "bar"] as String[]
                        }, getProperty:{def key -> "42"}
                    ] as ServiceReference
                ] as ServiceReference[]
            }

        ] as BundleContext

        def servlet = new ServiceServlet(bundleContext)

        StringWriter stringWriter = new StringWriter()

        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{def a ->
                assertThat a, is(equalTo("application/json"))
            }
        ] as HttpServletResponse

        def request = [
            getPathInfo:{ "/42" }
        ] as HttpServletRequest

        servlet.doGet(request, response)

        assertThat stringWriter.buffer.toString(), containsString(
                '''{"properties":{"Foo":"42","bar":"42"},"bundle":"com.test.bundle","usingBundles":[]}''')
    }

    @Test
    void 'call GET with invalid service id'() {

        BundleContext bundleContext = [getServiceReferences:{ def a, def b ->
                null
            } ] as BundleContext

        def servlet = new ServiceServlet(bundleContext)

        StringWriter stringWriter = new StringWriter()
        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{
            },
            setStatus:{ int status ->
                assertThat status, is(404)
            }
        ] as HttpServletResponse

        def request = [
            getPathInfo:{ "/42" }
        ] as HttpServletRequest

        servlet.doGet(request, response)

        assertThat stringWriter.buffer.toString(), containsString('''No Service Found''')
    }
}
