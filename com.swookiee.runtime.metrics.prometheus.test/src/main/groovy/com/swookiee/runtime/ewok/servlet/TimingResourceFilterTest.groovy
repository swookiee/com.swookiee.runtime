/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Ullrich - initial implementation
 *    Lars Pfannenschmidt - added threading tests 
 * *****************************************************************************
 */

package com.swookiee.runtime.ewok.servlet

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*
import io.prometheus.client.CollectorRegistry

import java.util.concurrent.CountDownLatch

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.core.MultivaluedHashMap

import org.glassfish.jersey.process.*
import org.glassfish.jersey.server.*
import org.glassfish.jersey.server.internal.process.*
import org.junit.Test

import com.swookiee.runtime.metrics.prometheus.TimingResourceFilter

public class TimingResourceFilterTest {

    @Test
    void 'simple call count test'(){
        CollectorRegistry collectorRegistry = [] as CollectorRegistry
        TimingResourceFilter filter = new TimingResourceFilter()
        filter.setCollectorRegistry(collectorRegistry)
        filter.activate()

        ContainerRequestContext tempRequestContext1 =[
            getMethod:{ "TEST1-1" }
        ] as ContainerRequestContext
        ContainerResponseContext tempResponseContext1 =[
            getHeaders:{
                new MultivaluedHashMap<String, Object>()
            },
            getMethod:{ "TEST1-1" },
            getStatus:{ 200 }
        ] as ContainerResponseContext

        ContainerRequestContext tempRequestContext2 =[
            getMethod:{ "TEST1-2" }
        ] as ContainerRequestContext
        ContainerResponseContext tempResponseContext2 =[
            getHeaders:{
                new MultivaluedHashMap<String, Object>()
            },
            getMethod:{ "TEST1-2" },
            getStatus:{ 200 }
        ] as ContainerResponseContext

        filter.filter(tempRequestContext1)
        filter.filter(tempRequestContext1, tempResponseContext1)

        (1..10).each{
            filter.filter(tempRequestContext2)
            filter.filter(tempRequestContext2, tempResponseContext2)
        }

        def sampleCount1 = (Integer)collectorRegistry.getSampleValue("requests_latency_seconds_count",
                (String[])[
                    "method",
                    "resource",
                    "status"
                ], (String[])[
                    "TEST1-1",
                    "undefined.undefined",
                    "200"
                ])
        def sampleCount2 = (Integer)collectorRegistry.getSampleValue("requests_latency_seconds_count",
                (String[])[
                    "method",
                    "resource",
                    "status"
                ], (String[])[
                    "TEST1-2",
                    "undefined.undefined",
                    "200"
                ])

        assertThat sampleCount1, is(equalTo(1))
        assertThat sampleCount2, is(equalTo(10))

        filter.deactivate()
        filter.unsetCollectorRegistry()
        collectorRegistry.clear()
    }

    @Test
    void 'test metric calculation multithreading'() {

        CollectorRegistry collectorRegistry = new CollectorRegistry()
        def filter = new TimingResourceFilter()
        filter.setCollectorRegistry(collectorRegistry)
        filter.activate()

        for (int j = 0; j < 20; ++j) {
            final CountDownLatch latch = new CountDownLatch(1)
            for (int i = 0; i < 50; ++i) {
                Runnable runner = new Runnable() {
                            public void run() {
                                try {
                                    latch.await()
                                    //force unique request mocks
                                    ContainerRequestContext tempRequestContext = [
                                        toString:{UUID.randomUUID()},
                                        getMethod:{ "GET" }
                                    ] as ContainerRequestContext
                                    ContainerResponseContext tempResponseContext =[
                                        getHeaders:{
                                            new MultivaluedHashMap<String, Object>()
                                        },
                                        getStatus:{ 200 }
                                    ] as ContainerResponseContext

                                    filter.filter(tempRequestContext)
                                    filter.filter(tempRequestContext, tempResponseContext)
                                } catch (InterruptedException ignored) {
                                }
                            }
                        }
                new Thread(runner, "TestThread" + i).start()
            }
            // all threads are waiting on the latch.
            latch.countDown() // release the latch
        }
        sleep 100

        def sampleCount = (Integer)collectorRegistry.getSampleValue("requests_latency_seconds_count",
                (String[])[
                    "method",
                    "resource",
                    "status"
                ], (String[])[
                    "GET",
                    "undefined.undefined",
                    "200"
                ])

        // We should have 1000 data points.
        assertThat sampleCount, is(equalTo(1000))

        // We should have no timers left, otherwise we will have memory-leaks
        assertThat filter.resourceRequestTimers.size(), is(equalTo(0))
    }
}
