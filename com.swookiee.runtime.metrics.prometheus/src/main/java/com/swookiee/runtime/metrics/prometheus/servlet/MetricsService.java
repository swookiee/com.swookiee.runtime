/**
 * *****************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tobias Ullrich - initial implementation
 *    Frank Wisniewski - single endpoint for each bundle registering a metric
 *                     - endpoint listing bundles containing metrics
 * *****************************************************************************
 */
package com.swookiee.runtime.metrics.prometheus.servlet;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swookiee.runtime.metrics.prometheus.CollectorRegistryInventory;

@Component
public class MetricsService implements Metrics {

    private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private CollectorRegistryInventory inventory;

    @Activate
    public void activate() {
        logger.info("Activated metrics service!");
    }

    @Deactivate
    public void deactivate() {
        logger.info("Deactivated metrics service!");
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(CollectorRegistryInventory inventory) {
        this.inventory = inventory;
    }

    public void unsetMetricRegistry(CollectorRegistryInventory inventory) {
        this.inventory = null;
    }

    @Override
    public Response metrics(String bundle) {
        CollectorRegistry registry = inventory.getCollectorRegistry(bundle);
        if (registry == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("Bundle '%s' not in inventory", bundle))
                    .build();
        }

        try {
            StringWriter writer = new StringWriter();
            TextFormat.write004(writer, registry.metricFamilySamples());
            return Response.ok(writer.toString(), TextFormat.CONTENT_TYPE_004).build();
        } catch (IOException ex) {
            logger.error(
                    String.format("Could not read from prometheus registry for bundle %s: %s", bundle, ex.getMessage()),
                    ex);
            return Response.serverError().build();
        }
    }

    @Override
    public Response plainBundleList() {
        String plain = reduce(inventory.getRegisteredBundles());
        return Response.ok(plain, MediaType.TEXT_PLAIN_TYPE).build();
    }

    @Override
    public Response htmlBundleList(UriInfo uri) {
        String linkTemplate = "<a href=" + uri.getAbsolutePath().toString();

        if (uri.getAbsolutePath().toString().endsWith("/")) {
            linkTemplate += "%s>%s</a><br />";
        } else {
            linkTemplate += "/%s>%s</a><br />";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("<html><head></head><body>");
        for (String bundle : inventory.getRegisteredBundles()) {
            builder.append(String.format(linkTemplate, bundle, bundle));
        }
        builder.append("</body></html>");
        return Response.ok(builder.toString(), MediaType.TEXT_HTML_TYPE).build();
    }

    @Override
    public Response jsonBundleList() {
        try {
            String json = mapper.writeValueAsString(inventory.getRegisteredBundles());
            return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (IOException ex) {
            logger.error("Could not serialize bundle array: " + ex.getMessage(), ex);
            return Response.serverError().build();
        }
    }

    private String reduce(Set<String> strings) {
        Iterator<String> iterator = strings.iterator();
        if (!iterator.hasNext()) {
            return "";
        }
        String value = iterator.next();
        while (iterator.hasNext()) {
            value = reduce(value, iterator.next());
        }
        return value;
    }

    private String reduce(String s1, String s2) {
        return String.format("%s, %s", s1, s2);
    }
}