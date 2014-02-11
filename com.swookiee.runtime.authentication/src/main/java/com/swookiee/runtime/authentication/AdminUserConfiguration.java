package com.swookiee.runtime.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AdminUserConfiguration {

    public static final String pid = "com.swookiee.runtime.authentication";

    public String username;

    public String password;

}
