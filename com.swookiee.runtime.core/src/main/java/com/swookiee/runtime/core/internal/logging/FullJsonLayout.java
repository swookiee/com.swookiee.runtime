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

package com.swookiee.runtime.core.internal.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
//import org.joda.time.*;

public class FullJsonLayout extends JsonLayout {

    @Override
    protected Map<String, Object> toJsonMap(final ILoggingEvent event) {
        @SuppressWarnings("unchecked")
        Map<String, Object> jsonMap = super.toJsonMap(event);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'");
        jsonMap.put("ts", df.format(new Date()));

        if (isMeantToBeLoggedAsFullJson(event)) {
            jsonMap.put("message", event.getArgumentArray()[0]);
        }

        return jsonMap;
    }

    private boolean isMeantToBeLoggedAsFullJson(final ILoggingEvent event) {
        Object[] args = event.getArgumentArray();
        return event.getMessage().equals("{}") && args.length == 1 && args[0] instanceof Map;
    }
}
