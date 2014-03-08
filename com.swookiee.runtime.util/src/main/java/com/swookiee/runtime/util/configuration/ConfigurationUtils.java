package com.swookiee.runtime.util.configuration;

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
            final Field[] fields = configuration.getClass().getFields();

            for (final Field field : fields) {

                if (!(field.get(configuration) instanceof Configuration)) {
                    logger.error("Field {} is not implementing {} and could not be applied!", field.getName(),
                            Configuration.class.getName());
                    return;
                }

                final Configuration configElement = (Configuration) field.get(configuration);

                @SuppressWarnings("unchecked")
                final Dictionary<String, Object> properties = mapper.convertValue(configElement, Hashtable.class);

                ConfigurationUtils.sendConfigurationToConfigAdmin(configurationAdmin, configElement.getPid(),
                        properties);

            }
        } catch (final IOException | IllegalArgumentException | IllegalAccessException ex) {
            logger.error("Could not apply configuration: " + ex.getMessage(), ex);
        }
    }

    private static void sendConfigurationToConfigAdmin(final ConfigurationAdmin configurationAdmin, final String pid,
            final Dictionary<String, ?> properties) throws IOException {
        final org.osgi.service.cm.Configuration configuration = configurationAdmin.getConfiguration(pid);
        configuration.update(properties);
    }

}
