package com.expediagroup.dropwizard.prometheus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.prometheus.client.Collector;
import io.prometheus.client.dropwizard.samplebuilder.DefaultSampleBuilder;

public class GlobalLabelSampleBuilderDecoratorTest {

    private DefaultSampleBuilder defaultSampleBuilder = new DefaultSampleBuilder();

    @Test
    public void testSimpleLabel(){
        Map<String, String> fixedlabels = new HashMap<>();
        fixedlabels.put("app_name", "my_app");

        GlobalLabelSampleBuilderDecorator decorator = new GlobalLabelSampleBuilderDecorator(defaultSampleBuilder, fixedlabels);

        Collector.MetricFamilySamples.Sample sample = decorator.createSample("com.test.count", "", Collections.emptyList(), Collections.emptyList(), 200);

        Assertions.assertThat(sample).isNotNull();
        Assertions.assertThat(sample.labelNames).isNotNull().hasSize(1).contains("app_name");
        Assertions.assertThat(sample.labelValues).isNotNull().hasSize(1).contains("my_app");
    }

    @Test
    public void testMultiLabel(){
        Map<String, String> fixedlabels = new HashMap<>();
        fixedlabels.put("label1", "value1");
        fixedlabels.put("label2", "value2");

        GlobalLabelSampleBuilderDecorator decorator = new GlobalLabelSampleBuilderDecorator(defaultSampleBuilder, fixedlabels);

        Collector.MetricFamilySamples.Sample sample = decorator.createSample("com.test.count", "", Collections.emptyList(), Collections.emptyList(), 200);

        Assertions.assertThat(sample).isNotNull();
        Assertions.assertThat(sample.labelNames).isNotNull().hasSize(2).contains("label1", "label2");
        Assertions.assertThat(sample.labelValues).isNotNull().hasSize(2).contains("value1", "value2");
    }

    @Test
    public void testAdditive(){
        Map<String, String> fixedlabels = new HashMap<>();
        fixedlabels.put("label1", "value1");
        fixedlabels.put("label2", "value2");

        List<String> dynamiclabelnames = Arrays.asList(new String[]{"john", "jane"});
        List<String> dynamiclabelvalues = Arrays.asList(new String[]{"doe", "dove"});

        GlobalLabelSampleBuilderDecorator decorator = new GlobalLabelSampleBuilderDecorator(defaultSampleBuilder, fixedlabels);

        Collector.MetricFamilySamples.Sample sample = decorator.createSample("com.test.count", "", dynamiclabelnames, dynamiclabelvalues, 200);

        Assertions.assertThat(sample).isNotNull();
        Assertions.assertThat(sample.labelNames).isNotNull().hasSize(4).contains("label1", "label2", "john", "jane");
        Assertions.assertThat(sample.labelValues).isNotNull().hasSize(4).contains("value1", "value2", "doe", "dove");
    }
}
