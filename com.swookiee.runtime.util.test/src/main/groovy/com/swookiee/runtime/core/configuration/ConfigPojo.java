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

package com.swookiee.runtime.core.configuration;

import com.swookiee.runtime.util.configuration.Configuration;

public class ConfigPojo implements Configuration {

    public static final String pid = "test";

    public String foo;

    @Override
    public String getPid() {
        return ConfigPojo.pid;
    }

}
