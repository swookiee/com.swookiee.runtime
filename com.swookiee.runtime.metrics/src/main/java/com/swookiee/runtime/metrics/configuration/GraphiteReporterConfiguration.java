package com.swookiee.runtime.metrics.configuration;

import com.swookiee.runtime.util.configuration.Configuration;

public class GraphiteReporterConfiguration implements Configuration {

    public static final String pid = "com.swookiee.runtime.metrics";

    public String graphiteHost;
    public Integer graphitePort;
    public Integer reportingIntervalInSeconds;
    public Boolean reportingEnabled;
    public String reportingPrefix = "";

    @Override
    public String getPid() {
        return GraphiteReporterConfiguration.pid;
    }

    @Override
    public String toString() {
        return "GraphiteReporterConfiguration [graphiteHost=" + graphiteHost + ", graphitePort=" + graphitePort
                + ", reportingIntervalInSeconds=" + reportingIntervalInSeconds + ", reportingEnabled="
                + reportingEnabled + ", reportingPrefix=" + reportingPrefix + "]";
    }
}
