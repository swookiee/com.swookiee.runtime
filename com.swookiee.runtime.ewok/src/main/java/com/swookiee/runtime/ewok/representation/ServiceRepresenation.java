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

package com.swookiee.runtime.ewok.representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceRepresenation {

    Map<String, Object> properties = new HashMap<>();
    String bundle;
    List<String> usingBundles = new ArrayList<>();

    public ServiceRepresenation() {
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(final Map<String, Object> properties) {
        this.properties = properties;
    }

    public void addProperty(final String key, final Object value) {
        properties.put(key, value);
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(final String bundle) {
        this.bundle = bundle;
    }

    public List<String> getUsingBundles() {
        return usingBundles;
    }

    public void setUsingBundles(final List<String> usingBundles) {
        this.usingBundles = usingBundles;
    }

    public void addUsingBundle(final String usingBundle) {
        usingBundles.add(usingBundle);
    }

}
