package com.swookiee.runtime.metrics.prometheus.servlet;

import com.swookiee.runtime.metrics.prometheus.SwookieeCollectorRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import java.io.IOException;
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.core.Response;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component
public class MetricsService implements Metrics {

    private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);

    private CollectorRegistry collectorRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setMetricRegistry(final SwookieeCollectorRegistry collectorRegistry) {
        this.collectorRegistry = collectorRegistry;
    }

    public void unsetMetricRegistry(final SwookieeCollectorRegistry collectorRegistry) {
        this.collectorRegistry = null;
    }

    @Activate
    public void activate() {
        logger.info("Activate Status Code Filter!");
    }

    @Deactivate
    public void deactivate() {
        logger.info("Deactivated Status Code Filter!");
    }

    @Override
    public Response metrics() {
        try {
            StringWriter writer = new StringWriter();
            TextFormat.write004(writer, collectorRegistry.metricFamilySamples());
            //Response.status(Response.Status.OK).type(TextFormat.CONTENT_TYPE_004).
            return Response.ok(writer.toString(), TextFormat.CONTENT_TYPE_004).build();

        } catch (IOException ex) {
            logger.error(null, ex);
            return Response.serverError().build();
        }
    }
}
