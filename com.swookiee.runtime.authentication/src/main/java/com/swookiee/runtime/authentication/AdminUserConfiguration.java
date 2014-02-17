package com.swookiee.runtime.authentication;

import com.swookiee.core.configuration.Configuration;

public class AdminUserConfiguration implements Configuration {

    public static final String pid = "com.swookiee.runtime.authentication";

    public String username;

    public String password;

    @Override
    public String getPid() {
        return pid;
    }
}
