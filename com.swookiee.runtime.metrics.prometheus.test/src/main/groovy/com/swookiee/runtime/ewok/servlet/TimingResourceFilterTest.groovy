/*******************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package com.swookiee.runtime.ewok.servlet

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import java.util.concurrent.CountDownLatch

import java.lang.reflect.Method;

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.UriInfo

import org.glassfish.jersey.server.model.ResourceMethodInvoker;

import org.glassfish.jersey.process.*
import org.glassfish.jersey.server.*
import org.glassfish.jersey.server.internal.process.*
import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import org.junit.Test

import com.swookiee.runtime.metrics.prometheus.SwookieeCollectorRegistry
import com.swookiee.runtime.metrics.prometheus.TimingResourceFilter

import io.prometheus.client.CollectorRegistry;

public class TimingResourceFilterTest {

    @Test
    void 'simple call count test'(){
        CollectorRegistry collectorRegistry = [] as CollectorRegistry
        TimingResourceFilter filter = new TimingResourceFilter()
        filter.setMetricRegistry(collectorRegistry)
        filter.activate()

        ContainerRequestContext tempRequestContext1 =[
                getMethod:{"TEST1-1"}
        ] as ContainerRequestContext
        ContainerResponseContext tempResponseContext1 =[
            getHeaders:{
                new MultivaluedHashMap<String, Object>()
            },
            getMethod:{"TEST1-1"},
            getStatus:{200}
        ] as ContainerResponseContext

        ContainerRequestContext tempRequestContext2 =[
                getMethod:{"TEST1-2"}
        ] as ContainerRequestContext
        ContainerResponseContext tempResponseContext2 =[
                getHeaders:{
                    new MultivaluedHashMap<String, Object>()
                },
                getMethod:{"TEST1-2"},
                getStatus:{200}
        ] as ContainerResponseContext

        filter.filter(tempRequestContext1)
        filter.filter(tempRequestContext1, tempResponseContext1)

        (1..10).each{
            filter.filter(tempRequestContext2)
            filter.filter(tempRequestContext2, tempResponseContext2)
        }

        def sampleCount1 = (Integer)collectorRegistry.getSampleValue("requests_latency_seconds_count",
                (String[])["method", "resource", "status"], (String[])["TEST1-1", "undefined.undefined", "200"])
        def sampleCount2 = (Integer)collectorRegistry.getSampleValue("requests_latency_seconds_count",
                (String[])["method", "resource", "status"], (String[])["TEST1-2", "undefined.undefined", "200"])

        assertThat sampleCount1, is(equalTo(1))
        assertThat sampleCount2, is(equalTo(10))

        filter.deactivate()
        filter.unsetMetricRegistry()
        collectorRegistry.clear()
    }
}
