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

package com.swookiee.runtime.authentication;

import com.swookiee.runtime.util.configuration.Configuration;

public class AdminUserConfiguration implements Configuration {

    public static final String pid = "com.swookiee.runtime.authentication";

    public String username;

    public String password;

    @Override
    public String getPid() {
        return pid;
    }
}
