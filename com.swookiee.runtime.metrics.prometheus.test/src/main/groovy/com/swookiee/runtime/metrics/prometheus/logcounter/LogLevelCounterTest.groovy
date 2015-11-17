package com.swookiee.runtime.metrics.prometheus.logcounter

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent

import com.swookiee.runtime.metrics.prometheus.BaseCollectorRegistryTest

public class LogLevelCounterTest extends BaseCollectorRegistryTest {
	
    private static final Logger logger = LoggerFactory.getLogger(LogLevelCounterTest.class)
    def LogLevelCounter counter
    def ILoggingEvent event

    @Before
    void setUp() {
        counter = new LogLevelCounter()
        counter.activate(bundleContextMock)
    }

    @After
    void tearDown(){
        counter.deactivate()
    }

    @Test
    void meterTrace() {
        event = [getLevel:{Level.TRACE}] as ILoggingEvent
        counter.appender.doAppend(event)
        assertThat(getCount("trace"), is(equalTo(1.0d)))
    }
    
    @Test
    void meterDebug() {
        event = [getLevel:{Level.DEBUG}] as ILoggingEvent
        counter.appender.doAppend(event)
        assertThat(getCount("debug"), is(equalTo(1.0d)))
    }
	
	@Test
	void meterInfo() {
		event = [getLevel:{Level.INFO}] as ILoggingEvent
		counter.appender.doAppend(event)
		assertThat(getCount("info"), is(equalTo(1.0d)))
	}

	@Test
	void meterWarn() {
		event = [getLevel:{Level.WARN}] as ILoggingEvent
		counter.appender.doAppend(event)
		assertThat(getCount("warn"), is(equalTo(1.0d)))
	}
	
	@Test
	void meterError() {
		event = [getLevel:{Level.ERROR}] as ILoggingEvent
		counter.appender.doAppend(event)
		assertThat(getCount("error"), is(equalTo(1.0d)))
	}

    def getCount(String level){
        collectorRegistry.getSampleValue("logback_loglevel_total", (String[])["level"], (String[])[level])
    }
}
