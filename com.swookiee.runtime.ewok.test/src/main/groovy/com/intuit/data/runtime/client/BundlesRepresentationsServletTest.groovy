package com.intuit.data.runtime.client;

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import javax.servlet.http.HttpServletResponse

import org.junit.Test
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.Version

import com.intuit.data.runtime.client.servlet.BundlesRepresentationsServlet

public class BundlesRepresentationsServletTest {

    @Test
    void 'call GET and return bundle list representation'(){

        def servlet = new BundlesRepresentationsServlet(bundleContextMock);

        StringWriter stringWriter = new StringWriter()
        def response = [
            getWriter:{ new PrintWriter(stringWriter) },
            setContentType:{
            }
        ] as HttpServletResponse

        servlet.doGet(null, response)

        assertThat stringWriter.buffer.toString(), containsString(
                '''[{"id":1,"lastModified":1,"location":"hypernet","state":42,"symbolicName":"foobar","version":"42.0.0"}]''')
    }


    def getBundleContextMock(){
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

        bundleContext
    }
}
