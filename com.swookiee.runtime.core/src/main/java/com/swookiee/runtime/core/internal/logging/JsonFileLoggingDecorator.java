/*******************************************************************************
 * Copyright (c) 2014 Thorsten Krüger, Lars Pfannenschmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thorsten Krüger - initial API and implementation, ongoing development and documentation
 *    Lars Pfannenschmidt - minor changes for configuration
 *******************************************************************************/

package com.swookiee.runtime.core.internal.logging;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.jackson.JacksonJsonFormatter;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

/**
 * When active (system property 'productionLogging' is non-null), this adds an appender to the Logback root logger. This
 * appender will append any log messages to a log file in JSON format.
 * <p>
 * Simple message will automatically be logged in json format, for example <code>logger.info("just text")</code> will
 * give <code>{ ... "message":"just text" ...}</code>.
 * <p>
 * Complex types can be given in a format like this:
 * 
 * <code><pre>
 * Map<String, Object> map = new HashMap<>();
 * map.put("string", "string value");
 * map.put("myBean", new MyBean());
 * logger.info("{}", map);
 * </pre></code> If
 * <code>myBean</cdoe> is a proper JavaBean, the message will be logged as json, including the serialzed instance of <code>MyBean</code>.
 */
public class JsonFileLoggingDecorator {

    private static final Logger logger = LoggerFactory.getLogger(JsonFileLoggingDecorator.class);
    private DeploymentLogging deploymentLogging;

    /**
     * Holds configuration parameters for deployment logging.
     */
    public static class DeploymentLogging {
        /** If this is non-null, the deployment logging is active. */
        private final static String ACTIVATION = "productionLogging";
        /** If set, this is the directory logs will be generated in. */
        private final static String LOGGING_DIRECTORY = "loggingDirectory";

        private String loggingDirectory;
        private final boolean active;
        private final String filenamePattern;
        private final int maxHistory = 30;
        private final String file;

        public DeploymentLogging(final BundleContext context) {
            loggingDirectory = context.getProperty(LOGGING_DIRECTORY);
            if (loggingDirectory == null || loggingDirectory.length() == 0) {
                loggingDirectory = ".";
            }
            filenamePattern = loggingDirectory + "/osgi-log.%d{yyyy-MM-dd}.json";
            file = loggingDirectory + "/osgi-log.json";

            active = Boolean.parseBoolean(context.getProperty(ACTIVATION));
        }

        public String getLoggingDirectory() {
            return loggingDirectory;
        }

        public boolean isActive() {
            return active;
        }

        public String getFilenamePattern() {
            return filenamePattern;
        }

        public int getMaxHistory() {
            return maxHistory;
        }

        public String getFile() {
            return file;
        }

    }

    public void install(final BundleContext context) {
        deploymentLogging = new DeploymentLogging(context);
        if (deploymentLogging.isActive()) {
            addJsonAppenderToRootLogger();
            logger.info("deployment logging activated");
        } else {
            logger.info("NOT setting up deployment logging. Set property '" + DeploymentLogging.ACTIVATION
                    + "' to activate.");
        }
    }

    private void addJsonAppenderToRootLogger() {
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("root");
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setFile(deploymentLogging.getFile());

        TimeBasedRollingPolicy<Object> policy = new TimeBasedRollingPolicy<>();
        policy.setContext(loggerContext);
        policy.setParent(appender);
        policy.setFileNamePattern(deploymentLogging.getFilenamePattern());
        policy.setMaxHistory(deploymentLogging.getMaxHistory());
        policy.start();

        appender.setRollingPolicy(policy);

        final LayoutWrappingEncoder<ILoggingEvent> wrappingEncoder = new LayoutWrappingEncoder<>();
        FullJsonLayout jsonLayout = new FullJsonLayout();
        wrappingEncoder.setLayout(jsonLayout);

        JacksonJsonFormatter formatter = new JacksonJsonFormatter();
        formatter.setPrettyPrint(false);
        jsonLayout.setJsonFormatter(formatter);
        jsonLayout.setAppendLineSeparator(true);

        appender.setContext(loggerContext);
        appender.setEncoder(wrappingEncoder);
        appender.start();

        rootLogger.addAppender(appender);
    }
}
