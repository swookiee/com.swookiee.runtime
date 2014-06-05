/*******************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lars Pfannenschmidt - initial API and implementation, ongoing development and documentation
 *******************************************************************************/

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
