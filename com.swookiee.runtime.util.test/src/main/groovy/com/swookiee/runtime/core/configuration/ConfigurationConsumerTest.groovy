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

package com.swookiee.runtime.core.configuration

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import org.junit.Test

import com.swookiee.runtime.util.configuration.ConfigurationConsumer

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
