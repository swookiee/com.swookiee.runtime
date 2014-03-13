package com.swookiee.runtime.util.internal;

import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.filter.LoggingFilter;
import org.osgi.service.component.annotations.Component;

@Component(service = {LoggingFilter.class})
@Provider
public class RequestLoggingFilter extends LoggingFilter {

}
