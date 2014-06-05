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

public class BundleStartlevelRepresentation {

    private int startLevel;
    private Boolean activationPolicyUsed;
    private Boolean persistentlyStarted;

    public BundleStartlevelRepresentation() {
    }

    public BundleStartlevelRepresentation(final int startLevel, final Boolean activationPolicyUsed, final Boolean persistentlyStarted) {
        this.startLevel = startLevel;
        this.activationPolicyUsed = activationPolicyUsed;
        this.persistentlyStarted = persistentlyStarted;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(final int startLevel) {
        this.startLevel = startLevel;
    }

    public Boolean getActivationPolicyUsed() {
        return activationPolicyUsed;
    }

    public void setActivationPolicyUsed(final Boolean activationPolicyUsed) {
        this.activationPolicyUsed = activationPolicyUsed;
    }

    public Boolean getPersistentlyStarted() {
        return persistentlyStarted;
    }

    public void setPersistentlyStarted(final Boolean persistentlyStarted) {
        this.persistentlyStarted = persistentlyStarted;
    }

}
