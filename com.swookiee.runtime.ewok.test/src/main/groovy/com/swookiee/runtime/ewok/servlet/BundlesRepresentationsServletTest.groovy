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

import javax.servlet.http.HttpServletResponse

import org.junit.Test
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.Version

public class BundlesRepresentationsServletTest extends BaseServletTest {

    @Test
    void 'call GET and return bundle list representation'(){

        def servlet = new BundlesRepresentationsServlet(bundleContextMock)

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
