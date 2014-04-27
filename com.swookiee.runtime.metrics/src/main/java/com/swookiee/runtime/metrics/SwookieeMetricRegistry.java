package com.swookiee.runtime.metrics;

import org.osgi.service.component.annotations.Component;

import com.codahale.metrics.MetricRegistry;

@Component(service = {MetricRegistry.class, SwookieeMetricRegistry.class}, property = {"metricPrefix=swookiee"})
public class SwookieeMetricRegistry extends MetricRegistry {

}
