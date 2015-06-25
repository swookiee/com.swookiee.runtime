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

import java.util.concurrent.CountDownLatch

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.core.MultivaluedHashMap

import org.glassfish.jersey.process.*
import org.glassfish.jersey.server.*
import org.glassfish.jersey.server.internal.process.*
import org.junit.Test

import com.codahale.metrics.Timer
import com.swookiee.runtime.metrics.SwookieeMetricRegistry
import com.swookiee.runtime.metrics.TimingResourceFilter

public class TimingResourceFilterTest {

    @Test
    void 'test simple one call metric calculation'(){
        SwookieeMetricRegistry metricRegistry = [] as SwookieeMetricRegistry
        def filter = createFilter(metricRegistry)

        ContainerRequestContext tempRequestContext = [toString:"test"] as ContainerRequestContext
        ContainerResponseContext tempResponseContext =[
            getHeaders:{
                new MultivaluedHashMap<String, Object>()
            }] as ContainerResponseContext

        filter.filter(tempRequestContext)
        assertThat filter.resourceRequestTimers.size(), is(equalTo(1))
        filter.filter(tempRequestContext, tempResponseContext)

        assertThat metricRegistry.getTimers().size(), is(equalTo(2))
        assertThat filter.resourceRequestTimers.size(), is(equalTo(0))
    }

    TimingResourceFilter createFilter(SwookieeMetricRegistry metricRegistry){
        TimingResourceFilter filter = new TimingResourceFilter()
        filter.setMetricRegistry(metricRegistry)
        filter.activate()
        filter
    }

    @Test
    void 'test metric calculation multithreading'() {

        SwookieeMetricRegistry metricRegistry = [] as SwookieeMetricRegistry
        def filter = createFilter(metricRegistry)

        for (int j = 0; j < 20; ++j) {
            final CountDownLatch latch = new CountDownLatch(1)
            for (int i = 0; i < 25; ++i) {
                Runnable runner = new Runnable() {
                            public void run() {
                                try {
                                    latch.await()
                                    //force unique request mocks
                                    ContainerRequestContext tempRequestContext = [toString:UUID.randomUUID()] as ContainerRequestContext
                                    ContainerResponseContext tempResponseContext =[
                                        getHeaders:{
                                            new MultivaluedHashMap<String, Object>()
                                        }] as ContainerResponseContext

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

        // We should have two timers, 500 data points each.
        assertThat filter.metricRegistry.getTimers().size(), is(equalTo(2))

        // We should have no timers left
        assertThat filter.resourceRequestTimers.size(), is(equalTo(0))

        Timer firstTimer = filter.metricRegistry.getTimers().values().first()
        Timer lastTimer = filter.metricRegistry.getTimers().values().last()
        assertThat firstTimer.count, is(equalTo(500L))
        assertThat lastTimer.count, is(equalTo(500L))
    }
}
