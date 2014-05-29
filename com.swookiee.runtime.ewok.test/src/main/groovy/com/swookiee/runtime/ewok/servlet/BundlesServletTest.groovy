

package com.swookiee.runtime.ewok.servlet

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.junit.Test
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.BundleException
import org.osgi.framework.Version
import org.osgi.framework.startlevel.BundleStartLevel

public class BundlesServletTest extends BaseServletTest {

    @Test
    void 'call GET and return bundle list representation'(){

        Bundle bundle = [
            getBundleId:{1L},
            getLastModified:{1L},
            getLocation:{"hypernet"},
            getState:{42},
            getSymbolicName:{"foobar"},
            getVersion:{new Version("42")}
        ] as Bundle

        BundleContext bundleContext = [
            getBundles:{
                [bundle] as Bundle[]
            }
        ] as BundleContext

        def servlet = new BundlesServlet(bundleContext)

        StringWriter stringWriter = new StringWriter()
        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{
            }
        ] as HttpServletResponse

        servlet.doGet(null, response)

        assertThat stringWriter.buffer.toString(), containsString(
                '''["/framework/bundle/1"]''')
    }

    @Test
    void 'call POST, installBundle and check if location was returned'(){

        BundleStartLevel startLevel = [
            setStartLevel:{ int a -> a } ] as BundleStartLevel

        BundleContext bundleContext = [
            installBundle:{ def url, def stream ->
                [ getBundleId:{1L},
                    adapt:{ startLevel }
                ] as Bundle
            },
            getBundle:{ def url -> null }
        ] as BundleContext

        def servlet = new BundlesServlet(bundleContext)

        StringWriter stringWriter = new StringWriter()
        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{
            }
        ] as HttpServletResponse

        def request = [
            getReader:{ new BufferedReader(new StringReader("www.foobar.de")) },
            getContentType:{"text/plain"},
            getHeader:{ def a -> null }
        ] as HttpServletRequest

        servlet.doPost(request, response)

        assertThat stringWriter.buffer.toString().trim(), is("/framework/bundle/1")
    }


    @Test
    void 'call POST where installBundle throws exception'(){

        BundleContext bundleContext = [
            getBundle:{ def url -> null},
            installBundle:{ def url, def stream ->
                throw new BundleException("No way this gets installed", 42)
            }
        ] as BundleContext

        def servlet = new BundlesServlet(bundleContext)

        StringWriter stringWriter = new StringWriter()
        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{
            },
            setStatus:{ int status ->
                assertThat status, is(500)
            }
        ] as HttpServletResponse

        def request = [
            getReader:{
                new BufferedReader(new StringReader("remote bundle location url"))
            },
            getContentType:{ "text/plain" },
            getHeader:{ def a -> null }
        ] as HttpServletRequest

        servlet.doPost(request, response)
        assertThat stringWriter.buffer.toString().trim(), is(equalTo('''{"typecode":42,"message":"No way this gets installed"}'''))
    }

    @Test
    void 'call POST without Content-Location'(){

        def servlet = new BundlesServlet(null)

        StringWriter stringWriter = new StringWriter()
        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{
            },
            setStatus:{ int status ->
                assertThat status, is(400)
            }
        ] as HttpServletResponse

        def request = [
            getContentType:{ "application/x-jar" },
            getHeader:{
            }
        ] as HttpServletRequest

        servlet.doPost(request, response)
        assertThat stringWriter.buffer.toString().trim(), is(equalTo('''File upload must contain Content-Location header declaring location'''))
    }

    @Test
    void 'call POST and install via stream, but bundle is already installed with same location'(){

        BundleContext bundleContext = [
            getBundle: {
                [getBundleId:{1L}] as Bundle
            }
        ] as BundleContext

        def servlet = new BundlesServlet(bundleContext)

        StringWriter stringWriter = new StringWriter()
        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{
            },
            setStatus:{ int status ->
                assertThat status, is(409)
            }
        ] as HttpServletResponse

        def request = [
            getContentType:{ "application/x-jar" },
            getHeader:{"Content-Location:foobar.jar"},
            getInputStream:{ null }
        ] as HttpServletRequest

        servlet.doPost(request, response)
        assertThat stringWriter.buffer.toString().trim(), is(equalTo('''Bundle with same location already installed'''))
    }

    @Test
    void 'call POST, installBundle via stream and check if location was returned'(){
        
        BundleStartLevel startLevel = [
            setStartLevel:{ int a -> a } ] as BundleStartLevel

        BundleContext bundleContext = [
            installBundle:{ def url, def stream ->
                [getBundleId:{1L},
                    adapt:{startLevel}] as Bundle
            },
            getBundle: { null }
        ] as BundleContext

        def servlet = new BundlesServlet(bundleContext)

        StringWriter stringWriter = new StringWriter()
        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{
            }
        ] as HttpServletResponse

        def request = [
            getReader:{ new BufferedReader(new StringReader("www.foobar.de")) },
            getContentType:{"application/x-jar"},
            getHeader:{"Content-Location:foobar.jar"},
            getInputStream:{ null }
        ] as HttpServletRequest

        servlet.doPost(request, response)

        assertThat stringWriter.buffer.toString().trim(), is("/framework/bundle/1")
    }
}
