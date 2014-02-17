package com.swookiee.core.configuration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
public final class ConfigurationUtils<T> {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);
    private final static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final Class<T> clazz;

    /**
     * 
     * @param clazz
     *            see {@link ConfigurationUtils}
     */
    public ConfigurationUtils(final Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 
     * @param configurationFile
     *            URL to your yaml configuration
     * @param configurationAdmin
     *            {@link ConfigurationAdmin} instance
     */
    public void setConfiguration(final URL configurationFile, final ConfigurationAdmin configurationAdmin) {
        try {

            final T configuration = mapper.readValue(configurationFile, clazz);
            final Field[] fields = configuration.getClass().getFields();

            for (final Field field : fields) {
                final Configuration configElement = (Configuration) field.get(configuration);

                @SuppressWarnings("unchecked")
                final Dictionary<String, Object> properties = mapper.convertValue(configElement, Hashtable.class);

                this.setConfig(configurationAdmin, configElement.getPid(), properties);

            }
        } catch (final IOException | IllegalArgumentException | IllegalAccessException ex) {
            logger.error("Could not apply configuration: " + ex.getMessage(), ex);
        }
    }

    private void setConfig(final ConfigurationAdmin configurationAdmin, final String pid,
            final Dictionary<String, ?> properties) throws IOException {
        final org.osgi.service.cm.Configuration configuration = configurationAdmin.getConfiguration(pid);
        configuration.update(properties);
    }

}
