//package com.swookiee.runtime.metrics.prometheus.logcounter;
//
//import static org.junit.Assert.assertEquals;
//
//import ch.qos.logback.classic.Level;
//import ch.qos.logback.classic.spi.ILoggingEvent;
//import io.prometheus.client.CollectorRegistry;
//import org.junit.Before;
//import org.junit.Test;
//
//public class LogLevelCounterTest {
//    def LogLevelCounterAppender appender;
//    def ILoggingEvent event;
//    def CollectorRegistry collectorRegistry = [] as CollectorRegistry
//
//    @Before
//    public void setUp() throws Exception {
//        appender = new LogLevelCounterAppender();
//        appender.start();
//    }
//
//    @Test
//    public void metersTraceEvents() throws Exception {
//        event = [getLevel:{Level.TRACE}] as ILoggingEvent
//        appender.doAppend(event);
//        assertEquals(1, getLogLevelCount("trace"));
//    }
//
//    private int getLogLevelCount(String level) {
//        return appender.counter.("counter", ["level"] as String[],[level] as String[]).intValue();
//    }
//}
