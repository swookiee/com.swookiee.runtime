package com.swookiee.core.internal.logging;

import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.swookiee.runtime.core.internal.logging.FullJsonLayout;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.jackson.JacksonJsonFormatter;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

public class FullJsonLayoutTest {

    private ByteArrayOutputStream outputStream;
    private Logger logger;

    @Before
    public void prepareLogger() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        final LayoutWrappingEncoder<ILoggingEvent> wrappingEncoder = new LayoutWrappingEncoder<>();
        FullJsonLayout jsonLayout = new FullJsonLayout();
        wrappingEncoder.setLayout(jsonLayout);

        JacksonJsonFormatter formatter = new JacksonJsonFormatter();
        formatter.setPrettyPrint(false);
        jsonLayout.setJsonFormatter(formatter);
        jsonLayout.setAppendLineSeparator(true);

        outputStream = new ByteArrayOutputStream();
        OutputStreamAppender<ILoggingEvent> appender = new OutputStreamAppender<>();

        appender.setName("outputStreamAppender");
        appender.setContext(lc);
        appender.setEncoder(wrappingEncoder);
        appender.setOutputStream(outputStream);
        appender.start();

        logger = (Logger) LoggerFactory.getLogger(FullJsonLayoutTest.class);

        logger.addAppender(appender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false); /* set to true if root should log too */
    }

    private String getLogOutput() {
        return new String(outputStream.toByteArray());
    }

    // this is a complete javaBean and can be serialized to json
    static class CanBeSerialized {
        String aString = "string value";
        int anInt = 456;

        public String getaString() {
            return aString;
        }

        public int getAnInt() {
            return anInt;
        }

    }

    // this class is missing getters, which makes jackson choke on it
    static class CanNotBeSerialized {
        String aString = "string value";
        int anInt = 456;

        @Override
        public String toString() {
            return "CanNotBeSerialized toString() value";
        }
    }

    @Test
    public void logsSimpleMessagesAsJsonString() throws Exception {
        logger.error("this {} my message", "is");
        assertThat(getLogOutput(), CoreMatchers.containsString("\"message\":\"this is my message\""));
    }

    @Test
    public void logsObjectOnlyMessagesAsJson() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("simpleTypeInt", 123);
        map.put("simpleTypeString", "text");
        map.put("anObject", new CanBeSerialized());
        logger.error("{}", map);
        assertThat(
                getLogOutput(),
                CoreMatchers.containsString("{\"anObject\":{\"aString\":\"string value\",\"anInt\":456},\"simpleTypeInt\":123,\"simpleTypeString\":\"text\"}"));
    }

    @Test
    public void fallsBackOnToStringWhenMessageCannotBeSerialized() throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("simpleTypeInt", 123);
        map.put("simpleTypeString", "text");
        map.put("anObject", new CanNotBeSerialized());
        logger.error("{}", map);
        System.out.println(getLogOutput());
        assertThat(
                getLogOutput(),
                CoreMatchers.containsString("{anObject=CanNotBeSerialized toString() value, simpleTypeInt=123, simpleTypeString=text}"));
    }

    @Test
    public void registersErrorFieldWhenMessageCannotBeSerialized() throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("simpleTypeInt", 123);
        map.put("simpleTypeString", "text");
        map.put("anObject", new CanNotBeSerialized());
        logger.error("{}", map);
        assertThat(
                getLogOutput(),
                CoreMatchers.containsString("{anObject=CanNotBeSerialized toString() value, simpleTypeInt=123, simpleTypeString=text}"));
    }

}
