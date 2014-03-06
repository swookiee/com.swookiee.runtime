package com.swookiee.runtime.authentication;

import com.swookiee.runtime.util.configuration.Configuration;

public class AdminUserConfiguration implements Configuration {

    public static final String pid = "com.swookiee.runtime.authentication";

    public String username;

    public String password;

    @Override
    public String getPid() {
        return pid;
    }
}
