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

package com.swookiee.runtime.util.configuration;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ConfigurationConsumer<T> {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, Object> configuration = new ConcurrentHashMap<>();
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private ConfigurationConsumer() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ConfigurationConsumer(final T defaultConfig) {
        final Hashtable defaultValues = mapper.convertValue(defaultConfig, Hashtable.class);
        configuration.putAll(defaultValues);
    }

    public static <T> ConfigurationConsumer<T> withDefaultConfiguration(final T defaultConfig) {
        return new ConfigurationConsumer<T>(defaultConfig);
    }

    public static <T> ConfigurationConsumer<T> newConsumer() {
        return new ConfigurationConsumer<T>();
    }

    public ConfigurationConsumer<T> applyConfiguration(final Map<String, ?> configuration) {
        this.configuration.putAll(configuration);
        return this;
    }

    public T getConfiguration(final Class<T> clazz) {
        return mapper.convertValue(configuration, clazz);
    }
}
