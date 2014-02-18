package com.swookiee.runtime.core.configuration;

import com.swookiee.core.configuration.Configuration;

public class ConfigPojo implements Configuration {

    public static final String pid = "test";

    public String foo;

    @Override
    public String getPid() {
        return ConfigPojo.pid;
    }

}
