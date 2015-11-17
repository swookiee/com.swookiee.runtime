/**
 * *****************************************************************************
 * Copyright (c) 2015 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lars Pfannenschmidt - initial implementation
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus.logcounter;

import io.prometheus.client.Counter;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class LogLevelCounterAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    public static final String COUNTER_NAME = "logback_loglevel_total";

    private final Counter counter;
    private final Counter.Child traceLabel;
    private final Counter.Child debugLabel;
    private final Counter.Child infoLabel;
    private final Counter.Child warnLabel;
    private final Counter.Child errorLabel;

    public LogLevelCounterAppender() {
        counter = Counter.build()
                .name(COUNTER_NAME)
                .help("Logback log statements at various log levels")
                .labelNames("level")
                .register();
        traceLabel = counter.labels("trace");
        debugLabel = counter.labels("debug");
        infoLabel = counter.labels("info");
        warnLabel = counter.labels("warn");
        errorLabel = counter.labels("error");
    }

    @Override
    protected void append(ILoggingEvent event) {
        switch (event.getLevel().toInt()) {
        case Level.TRACE_INT:
            traceLabel.inc();
            break;
        case Level.DEBUG_INT:
            debugLabel.inc();
            break;
        case Level.INFO_INT:
            infoLabel.inc();
            break;
        case Level.WARN_INT:
            warnLabel.inc();
            break;
        case Level.ERROR_INT:
            errorLabel.inc();
            break;
        default:
            break;
        }
    }

    public Counter getCounter() {
        return counter;
    }
}