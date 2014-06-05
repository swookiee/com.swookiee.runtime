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

public class FrameworkStartLevelRepresentation {

    private int startLevel;
    private int initialStartLevel;

    public FrameworkStartLevelRepresentation() {
    }

    public FrameworkStartLevelRepresentation(final int startLevel, final int initialStartLevel) {
        this.startLevel = startLevel;
        this.initialStartLevel = initialStartLevel;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(final int startLevel) {
        this.startLevel = startLevel;
    }

    public int getInitialStartLevel() {
        return initialStartLevel;
    }
}
