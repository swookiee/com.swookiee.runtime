package com.intuit.data.runtime.resource.representation;

public class BundleStartlevelRepresentation {

    private int startLevel;
    private Boolean activationPolicyUsed;
    private Boolean persistentlyStarted;

    public BundleStartlevelRepresentation(int startLevel, Boolean activationPolicyUsed, Boolean persistentlyStarted) {
        this.startLevel = startLevel;
        this.activationPolicyUsed = activationPolicyUsed;
        this.persistentlyStarted = persistentlyStarted;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(int startLevel) {
        this.startLevel = startLevel;
    }

    public Boolean getActivationPolicyUsed() {
        return activationPolicyUsed;
    }

    public void setActivationPolicyUsed(Boolean activationPolicyUsed) {
        this.activationPolicyUsed = activationPolicyUsed;
    }

    public Boolean getPersistentlyStarted() {
        return persistentlyStarted;
    }

    public void setPersistentlyStarted(Boolean persistentlyStarted) {
        this.persistentlyStarted = persistentlyStarted;
    }

}
