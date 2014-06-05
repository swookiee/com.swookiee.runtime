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

package com.swookiee.runtime.authentication.test

import static org.junit.Assert.*

import org.junit.Test
import org.osgi.framework.BundleContext
import org.osgi.service.useradmin.UserAdmin

import com.github.groovyosgi.testing.OSGiTest
import com.swookiee.runtime.authentication.AuthenticationService
import com.swookiee.runtime.authentication.internal.Activator

class AuthenticationServiceTest extends OSGiTest{

    @Test
    public void test() {
        UserAdmin useradmin = getService(UserAdmin)

        AuthenticationService authenticationService = getService(AuthenticationService)

        assertTrue authenticationService.validateUserCredentials("admin", "admin123")
        assertFalse authenticationService.validateUserCredentials("admin1", "test")
        assertFalse authenticationService.validateUserCredentials("admin", "test1")
    }

    @Override
    protected BundleContext getBundleContext() {
        return Activator.context
    }
}
