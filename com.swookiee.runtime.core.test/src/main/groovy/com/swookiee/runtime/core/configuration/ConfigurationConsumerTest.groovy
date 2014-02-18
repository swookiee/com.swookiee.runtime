package com.swookiee.runtime.core.configuration

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import org.junit.Test

import com.swookiee.core.configuration.ConfigurationConsumer

public class ConfigurationConsumerTest {

    @Test
    void 'transform config map to pojo'(){

        ConfigurationConsumer<ConfigPojo> consumer = ConfigurationConsumer.newConsumer().applyConfiguration(["foo":"bar"])

        def configuration = consumer.getConfiguration(ConfigPojo.class)

        assertThat configuration.foo, is(equalTo("bar"))
    }

    @Test
    void 'transform config map to pojo with defaults'(){

        ConfigPojo defaults = new ConfigPojo()
        defaults.foo = "barz"

        ConfigurationConsumer<ConfigPojo> consumer = ConfigurationConsumer.withDefaultConfiguration(defaults)
        def configuration = consumer.getConfiguration(ConfigPojo.class)

        assertThat configuration.foo, is(equalTo("barz"))
    }
}
