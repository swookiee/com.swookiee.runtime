/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Ullrich - initial implementation
 *    Frank Wisniewski - added endpoint to list metric bundles
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus.servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/metrics")
public interface Metrics {

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    Response plainBundleList();

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    Response htmlBundleList();

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Response jsonBundleList();

    @GET
    @Path("/{bundle}")
    @Produces
    Response metrics(@PathParam("bundle") String bundle);
}
