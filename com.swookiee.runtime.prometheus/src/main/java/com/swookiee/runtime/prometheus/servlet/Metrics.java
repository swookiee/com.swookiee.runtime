package com.swookiee.runtime.prometheus.servlet;

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
