package com.swookiee.runtime.ewok.representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceRepresenation {

    Map<String, Object> properties = new HashMap<>();
    String bundle;
    List<String> usingBundles = new ArrayList<>();

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public List<String> getUsingBundles() {
        return usingBundles;
    }

    public void setUsingBundles(List<String> usingBundles) {
        this.usingBundles = usingBundles;
    }

    public void addUsingBundle(String usingBundle) {
        usingBundles.add(usingBundle);
    }

}
