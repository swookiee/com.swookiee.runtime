/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus.servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public interface Metrics {

    @GET
    @Path("metrics")
    @Produces
    Response metrics();
}
