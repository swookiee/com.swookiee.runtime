/*******************************************************************************
 * Copyright (c) 2014 Thorsten Krüger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thorsten Krüger - initial API and implementation, ongoing development and documentation
 *******************************************************************************/

package com.swookiee.core.internal.logging;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.jackson.JacksonJsonFormatter;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swookiee.runtime.core.internal.logging.FullJsonLayout;

public class FullJsonLayoutTest {

    private ByteArrayOutputStream outputStream;
    private Logger logger;
    private ObjectMapper mapper = new ObjectMapper();

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
        assertThat(getLogOutput(), containsString("\"message\":\"this is my message\""));
    }

    @Test
    public void logsObjectOnlyMessagesAsJson() throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("simpleTypeInt", 123);
        map.put("simpleTypeString", "text");
        map.put("anObject", new CanBeSerialized());
        logger.error("{}", map);
        JsonNode root = mapper.readValue(getLogOutput(), JsonNode.class);
        assertThat(root.get("message").get("simpleTypeInt").asInt(), is(equalTo(123)));
        assertThat(root.get("message").get("simpleTypeString").asText(), is(equalTo("text")));
        assertThat(root.get("message").get("anObject").get("aString").asText(), is(equalTo("string value")));
        assertThat(root.get("message").get("anObject").get("anInt").asInt(), is(equalTo(456)));
    }

    @Test
    public void fallsBackOnToStringWhenMessageCannotBeSerialized() throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("simpleTypeInt", 123);
        map.put("simpleTypeString", "text");
        map.put("anObject", new CanNotBeSerialized());
        logger.error("{}", map);
        String logOutput = getLogOutput();
        assertThat(logOutput, containsString("anObject=CanNotBeSerialized toString() value"));
        assertThat(logOutput, containsString("simpleTypeInt=123"));
        assertThat(logOutput, containsString("impleTypeString=text"));
    }

    // TODO not sure this test is any different than fallsBackOnToStringWhenMessageCannotBeSerialized()?
    @Ignore
    public void registersErrorFieldWhenMessageCannotBeSerialized() throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("simpleTypeInt", 123);
        map.put("simpleTypeString", "text");
        map.put("anObject", new CanNotBeSerialized());
        logger.error("{}", map);
        assertThat(
                getLogOutput(),
                containsString("{anObject=CanNotBeSerialized toString() value, simpleTypeInt=123, simpleTypeString=text}"));
    }

}
