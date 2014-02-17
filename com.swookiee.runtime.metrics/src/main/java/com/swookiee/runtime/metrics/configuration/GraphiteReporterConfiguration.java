package com.swookiee.runtime.metrics.configuration;

import com.swookiee.core.configuration.Configuration;

public class GraphiteReporterConfiguration implements Configuration {

    public static final String pid = "com.swookiee.runtime.metrics.configuration";

    public String graphiteHost;
    public Integer graphitePort;
    public Integer reportingIntervalInSeconds;
    public Boolean reportingEnabled;

    @Override
    public String getPid() {
        return GraphiteReporterConfiguration.pid;
    }

    @Override
    public String toString() {
        return "GraphiteReporterConfiguration [graphiteHost=" + graphiteHost + ", graphitePort=" + graphitePort
                + ", reportingIntervalInSeconds=" + reportingIntervalInSeconds + ", reportingEnabled="
                + reportingEnabled + "]";
    }
}
