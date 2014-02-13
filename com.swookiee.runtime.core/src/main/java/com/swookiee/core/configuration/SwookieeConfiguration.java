package com.swookiee.core.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SwookieeConfiguration {
    @JsonIgnore
    String getPid();
}
