package com.swookiee.runtime.ewok.servlet

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.junit.Test
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.startlevel.FrameworkStartLevel

public class FrameworkStartLevelServletTest {

    @Test
    void 'call GET and return FrameworkStartLevelRepresentation'(){

        def servlet = new FrameworkStartLevelServlet(bundleContextMock)

        StringWriter stringWriter = new StringWriter()
        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{
            }
        ] as HttpServletResponse

        def request = [getContentType:{"application/json"}] as HttpServletRequest

        servlet.doGet(request, response)

        assertThat stringWriter.buffer.toString(), containsString('''{"startLevel":666,"initialStartLevel":1}''')
    }

    @Test
    void 'call PUT with invalid content type'(){
        StringWriter stringWriter = new StringWriter()

        def servlet = new FrameworkStartLevelServlet(bundleContextMock)

        def request = [getContentType:{"application/foobar"}] as HttpServletRequest

        def responsed = false
        def response = [
            sendError:{int errorCode ->
                assertThat errorCode, is(415)
                responsed = true
            }
        ] as HttpServletResponse

        servlet.doPut(request, response)
        assertThat responsed, is(true)
    }

    @Test
    void 'call PUT with invalid body'(){
        StringWriter stringWriter = new StringWriter()

        def servlet = new FrameworkStartLevelServlet(bundleContextMock)

        def reader = new BufferedReader(new StringReader("foobar"))

        def request = [getContentType:{"application/json"},
            getReader:{reader}] as HttpServletRequest

        def response = [
            sendError:{int code ->
                assertThat code, is(500)
            }
        ] as HttpServletResponse

        servlet.doPut(request, response)
    }

    @Test
    void 'call PUT with valid body and set startLevel and initialStartLevel'(){

        FrameworkStartLevel frameworkStartLevel = [
            getStartLevel:{666},
            getInitialBundleStartLevel:{1},
            setStartLevel:{def startLevel, def b -> assertThat startLevel, is(667)},
            setInitialBundleStartLevel:{def startLevel ->
                assertThat startLevel, is(2)
            }
        ] as FrameworkStartLevel

        StringWriter stringWriter = new StringWriter()

        def servlet = new FrameworkStartLevelServlet(getBundleContextMock(frameworkStartLevel))

        def reader = new BufferedReader(new StringReader('''{"startLevel":667,"initialStartLevel":2}'''))

        def request = [getContentType:{"application/json"},
            getReader:{reader}] as HttpServletRequest

        def response = [
            setStatus:{int code ->
                assertThat code, is(204)
            }
        ] as HttpServletResponse

        servlet.doPut(request, response)
    }

    /**
     * Shared bundle context mock for the default test case
     *
     * @return
     */
    def getBundleContextMock(FrameworkStartLevel frameworkStartLevel=defaultFrameworkStartLevelMock){

        Bundle systemBundle = [
            adapt:{frameworkStartLevel}
        ] as Bundle

        BundleContext bundleContext = [
            getBundle:{systemBundle}
        ] as BundleContext

        bundleContext
    }

    def getDefaultFrameworkStartLevelMock(){
        FrameworkStartLevel frameworkStartLevel = [
            getStartLevel:{666},
            getInitialBundleStartLevel:{1}
        ] as FrameworkStartLevel
        frameworkStartLevel
    }
}
