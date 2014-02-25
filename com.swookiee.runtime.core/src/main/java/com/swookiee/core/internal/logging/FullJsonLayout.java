package com.swookiee.core.internal.logging;

import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;

public class FullJsonLayout extends JsonLayout {

    @Override
    protected Map<String, Object> toJsonMap(final ILoggingEvent event) {
        @SuppressWarnings("unchecked")
        Map<String, Object> jsonMap = super.toJsonMap(event);

        if (isMeantToBeLoggedAsFullJson(event)) {
            jsonMap.put("message", event.getArgumentArray()[0]);
        }

        return jsonMap;
    }

    private boolean isMeantToBeLoggedAsFullJson(final ILoggingEvent event) {
        Object[] args = event.getArgumentArray();
        return event.getMessage().equals("{}") && args.length == 1 && args[0] instanceof HashMap;
    }
}
