package com.swookiee.runtime.core.configuration

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import org.junit.Test
import org.osgi.service.cm.Configuration
import org.osgi.service.cm.ConfigurationAdmin

import com.swookiee.core.configuration.ConfigurationUtils

public class ConfigurationUtilsTest {

    @Test
    void 'Configure via config admin'(){
        boolean tested = false

        ConfigurationAdmin configAdmin = [ getConfiguration : { def pid ->
                assertThat pid, equalTo(ConfigPojo.pid)
                [ update : { Dictionary properties ->
                        assertThat properties.get("foo"), equalTo("bar")
                        tested =  true
                    }
                ] as Configuration
            }] as ConfigurationAdmin

        URL configFile = this.getClass().getResource("UberConfig.yaml")

        ConfigurationUtils.applyConfiguration(UberConfig, configFile, configAdmin)

        assertThat tested, is(true)
    }
}
