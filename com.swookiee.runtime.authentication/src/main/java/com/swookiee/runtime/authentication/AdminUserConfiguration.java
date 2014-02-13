package com.swookiee.runtime.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.swookiee.core.configuration.SwookieeConfiguration;

@JsonInclude(Include.NON_NULL)
public class AdminUserConfiguration implements SwookieeConfiguration {

    public static final String pid = "com.swookiee.runtime.authentication";

    public String username;

    public String password;

    @Override
    public String getPid() {
        return pid;
    }
}
