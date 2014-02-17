package com.swookiee.core.configuration;

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

    public static <T> ConfigurationConsumer<T> withoutDefaultConfiguration() {
        return new ConfigurationConsumer<T>();
    }

    public void applyConfiguration(final Map<String, ?> configuration) {
        this.configuration.putAll(configuration);
    }

    public T getConfiguration(final Class<T> clazz) {
        return mapper.convertValue(configuration, clazz);
    }
}
