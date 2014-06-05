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

package com.swookiee.runtime.swaggerui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;

/**
 * This is a shared component for registering bundles how have the swagger documentation capability.
 */
@Component(service = {SwaggerDocumentationRegistry.class})
public class SwaggerDocumentationRegistry {

    private List<Bundle> registeredDocumentationBundles = Collections.synchronizedList(new ArrayList<Bundle>());

    public void register(Bundle bundle) {
        registeredDocumentationBundles.add(bundle);
    }

    public void unregister(Bundle bundle) {
        registeredDocumentationBundles.remove(bundle);
    }

    public List<Bundle> getRegisteredBundles() {
        return registeredDocumentationBundles;
    }
}
