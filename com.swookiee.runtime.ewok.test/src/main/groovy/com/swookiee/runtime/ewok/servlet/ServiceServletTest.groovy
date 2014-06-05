/*******************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lars Pfannenschmidt - initial API and implementation, ongoing development and documentation
 *******************************************************************************/



package com.swookiee.runtime.ewok.servlet

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*
import groovy.json.JsonSlurper

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.junit.Test
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceReference

public class ServiceServletTest extends BaseServletTest {

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

        def slurper = new JsonSlurper()
        def result = slurper.parseText(stringWriter.buffer.toString())
        assertThat result.properties.Foo, is(equalTo("42"))
        assertThat result.properties.bar, is(equalTo("42"))
        assertThat result.bundle, is(equalTo("com.test.bundle"))
        assertThat result.usingBundles.size, is(equalTo(0))
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

        assertThat stringWriter.buffer.toString(), containsString('''No Service with ID '42' Found''')
    }
}
