package com.swookiee.runtime.core.configuration

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test
import org.osgi.service.cm.Configuration
import org.osgi.service.cm.ConfigurationAdmin

import com.swookiee.runtime.util.configuration.ConfigurationUtils
import com.swookiee.runtime.util.test.Helpers

public class ConfigurationUtilsTest {

    @Test
    void 'Configure via config admin'(){
        boolean tested = false
        URL configFile = this.getClass().getResource("UberConfig.yaml")
        ConfigurationUtils.applyConfiguration(UberConfig, configFile, getConfigAdmin({ tested=true }))
        assertThat tested, is(true)
    }

    @Test
    void 'test that null values dont throw a NPE'(){
        ConfigurationUtils.applyConfiguration(null, null, null)
    }

    @Test
    void "gives a helpful error message when trying to apply non-simple types"() {
        try {
            ConfigurationUtils.applyConfigurationElements(
                    [ getConfiguration : { def pid ->
                            [ update : { Dictionary properties ->
                                    fail("should not update")
                                }
                            ] as Configuration
                        }] as ConfigurationAdmin, new Helpers.ConfigWrapper());
            fail("should have thrown Exception")
        } catch (RuntimeException e) {
            assertThat(e.getCause(), isA(java.lang.IllegalArgumentException))
            assertThat e.getCause().getMessage(), containsString('OSGi accepts only simple types')
            assertThat e.getCause().getMessage(), containsString('aMap')
        }
    }

    def getConfigAdmin(Closure<?> closure){
        def foo = [ getConfiguration : { def pid ->
                assertThat pid, equalTo(ConfigPojo.pid)
                [ update : { Dictionary properties ->
                        assertThat properties.get("foo"), equalTo("bar")
                        closure()
                    }
                ] as Configuration
            }] as ConfigurationAdmin
        return foo
    }
}
