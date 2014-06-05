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

public class BundleRepresentation {

    private long id;
    private long lastModified;
    private String location;
    private int state;
    private String symbolicName;
    private String version;

    public BundleRepresentation() {
    }

    public BundleRepresentation(final long id, final long lastModified, final String location, final int state, final String symbolicName,
            final String version) {
        this.id = id;
        this.lastModified = lastModified;
        this.location = location;
        this.state = state;
        this.symbolicName = symbolicName;
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(final long lastModified) {
        this.lastModified = lastModified;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public void setSymbolicName(final String symbolicName) {
        this.symbolicName = symbolicName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }
}