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

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.junit.Test
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.Version
import org.slf4j.Logger

public class BundleServletTest extends BaseServletTest {

    @Test
    void 'call GET and return bundle information'(){

        StringWriter stringWriter = new StringWriter()
        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{
            }
        ] as HttpServletResponse

        def request = [
            getPathInfo:{ "/42" }
        ] as HttpServletRequest


        new BundleServlet(bundleContextMock).doGet(request, response)

        assertThat stringWriter.toString().trim(),
                is('''{"id":1,"lastModified":1,"location":"hypernet","state":42,"symbolicName":"foobar","version":"foobar"}''')
    }

    @Test
    void 'call GET and return 404'(){

        def errorCalled = false

        BundleContext bundleContext = [
            getBundle:{ null }
        ] as BundleContext

        def response = [
            sendError:{ def errorCode->
                assertThat errorCode, is(404)
                errorCalled = true
            }
        ] as HttpServletResponse

        def request = [
            getPathInfo:{ "/42" }
        ] as HttpServletRequest

        new BundleServlet(bundleContext).doGet(request, response)
        assertThat errorCalled, is(true)
    }

    @Test
    void 'call PUT and update bundle'(){

        def updateBeenCalled = false

        def request = [
            getPathInfo:{ "/42" },
            getReader:{
                new BufferedReader(new StringReader(""))
            }
        ] as HttpServletRequest

        Bundle bundle = [
            update:{ updateBeenCalled=true }
        ] as Bundle

        BundleContext bundleContext = [
            getBundle:{ def id -> bundle }
        ] as BundleContext

        new BundleServlet(bundleContext).doPut(request, null)
        assertThat updateBeenCalled, is(true)
    }

    @Test
    void 'call PUT and update bundle via InputStream'(){

        def updateBeenCalled = false

        def request = [
            getPathInfo:{ "/42" },
            getReader:{
                new BufferedReader(new StringReader("file://"+System.getProperty("java.io.tmpdir")))
            }
        ] as HttpServletRequest

        Bundle bundle = [
            update:{ def stream ->
                assertThat stream, is(notNullValue())
                updateBeenCalled=true
            }
        ] as Bundle

        BundleContext bundleContext = [
            getBundle:{ def id -> bundle }
        ] as BundleContext

        new BundleServlet(bundleContext).doPut(request, null)
        assertThat updateBeenCalled, is(true)
    }

    @Test
    void 'call DELETE and uninstall bundle'(){

        def deleteBundleCalled = false

        def request = [
            getPathInfo:{ "/42" }
        ] as HttpServletRequest

        Bundle bundle = [
            uninstall:{deleteBundleCalled=true }
        ] as Bundle

        BundleContext bundleContext = [
            getBundle:{ def id -> bundle }
        ] as BundleContext

        new BundleServlet(bundleContext).doDelete(request, null)
        assertThat deleteBundleCalled, is(true)
    }

    def getBundleContextMock(){
        Bundle bundle = [
            getBundleId:{ 1L },
            getLastModified:{ 1L },
            getLocation:{ "hypernet" },
            getState:{ 42 },
            getSymbolicName:{ "foobar" },
            getVersion:{ new Version("42") }
        ] as Bundle

        BundleContext bundleContext = [
            getBundle:{ def id -> bundle }
        ] as BundleContext

        bundleContext
    }
}
