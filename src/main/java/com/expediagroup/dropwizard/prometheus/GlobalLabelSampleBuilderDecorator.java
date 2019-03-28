package com.expediagroup.dropwizard.prometheus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.prometheus.client.Collector;
import io.prometheus.client.dropwizard.samplebuilder.SampleBuilder;

/**
 * In prometheus land this is an anti-pattern. Ideally global labels should come from the prometheus server
 * which can be setup to re-label or enrich at scrapping time. It is considered an anti-pattern to use the client
 * side to enrich or add fixed labels.. That said with the advent of open metrics it is not always prometheus that
 * scrapes and there are certain times where the values need to come from the client.
 *
 * Dropwizard makes this particulary hard because there are no labels to begin with, the custom mapping namer can fix
 * some of this but that one just assumes that the values come from the actual metric name.
 *
 * This class just adds fixed tags to every sample.
 */
public class GlobalLabelSampleBuilderDecorator implements SampleBuilder {

    SampleBuilder decorate;
    ArrayList<String> labelNames;
    ArrayList<String> labelValues;

    public GlobalLabelSampleBuilderDecorator(SampleBuilder decorate, Map<String, String> labels){
        this.decorate = decorate;
        this.labelNames = new ArrayList<>(labels.keySet());
        this.labelValues = new ArrayList<>(labels.values());
    }

    @Override
    public Collector.MetricFamilySamples.Sample createSample(String dropwizardName, String nameSuffix, List<String> additionalLabelNames, List<String> additionalLabelValues, double value) {
        List<String> decoratedlabelnames = new ArrayList<>(additionalLabelNames);
        decoratedlabelnames.addAll(labelNames);
        List<String> decoratedlabelvalues = new ArrayList<>(additionalLabelValues);
        decoratedlabelvalues.addAll(labelValues);
        return decorate.createSample(dropwizardName, nameSuffix, decoratedlabelnames, decoratedlabelvalues, value);
    }
}
