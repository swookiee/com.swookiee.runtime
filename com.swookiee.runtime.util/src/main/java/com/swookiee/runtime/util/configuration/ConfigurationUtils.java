/*******************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt, Thorsten Krüger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lars Pfannenschmidt - initial API and implementation, ongoing development and documentation
 *    Thorsten Krüger - provide better error message on invalid configuration field types
 *******************************************************************************/

package com.swookiee.runtime.util.configuration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.swookiee.runtime.util.GuardAgainst;

/**
 * This utility class can be used to bridge your configuration information out of your YAML and POJO pair to the
 * ConfigurationAdmin and thereby to the providers of {@link Configuration} implementations.
 * <p>
 * Example:
 * 
 * <pre>
 * YourConfigPojo {
 *   public AdminUserConfiguration adminUserConfiguration;
 * }
 * </pre>
 * 
 * <p>
 * Corresponding YAML file
 * 
 * <pre>
 * adminUserConfiguration:
 *     username: "admin"
 *     password: "admin123"
 * </pre>
 * 
 * @param <T>
 *            Class of your configuration pojo.
 * 
 * 
 */
public final class ConfigurationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);
    private final static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * 
     * @param clazz
     *            Configuration POJO {@link Class}
     * @param configurationFile
     *            URL to your YAML configuration
     * @param configurationAdmin
     *            {@link ConfigurationAdmin} instance
     */
    public static <T> void applyConfiguration(final Class<T> clazz, final URL configurationFile,
            final ConfigurationAdmin configurationAdmin) {
        try {
            GuardAgainst.nullValue(clazz, "Class type can not be null");
            GuardAgainst.nullValue(configurationFile, "Configuration File URL can not be null");
            GuardAgainst.nullValue(configurationAdmin, "Configuration Admin reference can not be null");

            final T configuration = mapper.readValue(configurationFile, clazz);
            applyConfigurationElements(configurationAdmin, configuration);
        } catch (final IOException | IllegalArgumentException | IllegalAccessException ex) {
            logger.error("Could not apply configuration " + ex.getMessage(), ex);
        }
    }

    public static <T> void applyConfigurationElements(ConfigurationAdmin configurationAdmin, T configuration)
            throws IllegalAccessException, IOException {
        final Field[] fields = configuration.getClass().getFields();

        for (final Field field : fields) {

            if (!(field.get(configuration) instanceof Configuration)) {
                logger.error("Field {} is not implementing {} and could not be applied!", field.getName(),
                        Configuration.class.getName());
                continue;
            }

            final Configuration configElement = (Configuration) field.get(configuration);

            @SuppressWarnings("unchecked")
            final Dictionary<String, Object> properties = mapper.convertValue(configElement, Hashtable.class);

            ConfigurationUtils.sendConfigurationToConfigAdmin(configurationAdmin, configElement.getPid(), properties);
        }
    }

    private static void sendConfigurationToConfigAdmin(final ConfigurationAdmin configurationAdmin, final String pid,
            final Dictionary<String, ?> properties) throws IOException {
        final org.osgi.service.cm.Configuration configuration = configurationAdmin.getConfiguration(pid);
        try {
            validateConfiguration(properties);
            configuration.update(properties);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("failed to apply configuration " + pid, e);
        }
    }

    private static void validateConfiguration(final Dictionary<String, ?> properties) {
        Enumeration<String> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            assertValidType(key, properties.get(key));
        }
    }

    // copied over from Eclipse ConfigurationDictionary, to be able to provide a somewhat helpful
    // error message
    private static final Collection<Class<?>> simples = Arrays.asList(new Class<?>[]{String.class, Integer.class,
            Long.class, Float.class, Double.class, Byte.class, Short.class, Character.class, Boolean.class});
    private static final Collection<Class<?>> simpleArrays = Arrays.asList(new Class<?>[]{String[].class,
            Integer[].class, Long[].class, Float[].class, Double[].class, Byte[].class, Short[].class,
            Character[].class, Boolean[].class});
    private static final Collection<Class<?>> primitiveArrays = Arrays.asList(new Class<?>[]{long[].class, int[].class,
            short[].class, char[].class, byte[].class, double[].class, float[].class, boolean[].class});

    private static void assertValidType(String property, Object value) {
        Class<?> clazz = value.getClass();
        // Is it in the set of simple types
        if (simples.contains(clazz))
            return;
        // Is it an array of primitives or simples
        if (simpleArrays.contains(clazz) || primitiveArrays.contains(clazz))
            return;
        // Is it a Collection of simples
        if (value instanceof Collection) {
            Collection<?> valueCollection = (Collection<?>) value;
            for (Iterator<?> it = valueCollection.iterator(); it.hasNext();) {
                Class<?> containedClazz = it.next().getClass();
                if (!simples.contains(containedClazz)) {
                    throw new IllegalArgumentException(containedClazz.getName() + " in " + clazz.getName()
                            + ": OSGi only accepts arrays of simple types");
                }
            }
            return;
        }
        throw new IllegalArgumentException("OSGi accepts only simple types or arrays, but configuration property '"
                + property + "' is of type " + clazz.getName());
    }
}
