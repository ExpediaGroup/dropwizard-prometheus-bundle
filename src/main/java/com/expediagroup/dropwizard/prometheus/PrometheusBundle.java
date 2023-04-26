/* Copyright (c) 2018 Expedia Group.
 * All rights reserved.  http://www.homeaway.com

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expediagroup.dropwizard.prometheus;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.codahale.metrics.MetricRegistry;


import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.dropwizard.samplebuilder.CustomMappingSampleBuilder;
import io.prometheus.client.dropwizard.samplebuilder.DefaultSampleBuilder;
import io.prometheus.client.dropwizard.samplebuilder.MapperConfig;
import io.prometheus.client.dropwizard.samplebuilder.SampleBuilder;
import io.prometheus.client.exporter.MetricsServlet;
import lombok.extern.slf4j.Slf4j;

/**
 * @deprecated See <a href="https://github.com/ExpediaGroup/dropwizard-prometheus-bundle/blob/master/README.md">README.md</a>
 */
@Slf4j
@Deprecated
public class PrometheusBundle<T extends Configuration>  implements ConfiguredBundle<T> {

    Function<T, PrometheusBundleConfig> configMapperFn;
    CollectorRegistry prometheusRegistry;

    /**
    * @deprecated See <a href="https://github.com/ExpediaGroup/dropwizard-prometheus-bundle/blob/master/README.md">README.md</a>
    */
    @Deprecated
    public PrometheusBundle(Function<T, PrometheusBundleConfig> mapperFn){
        configMapperFn = mapperFn;
        prometheusRegistry = CollectorRegistry.defaultRegistry;
    }

    /**
    * @deprecated See <a href="https://github.com/ExpediaGroup/dropwizard-prometheus-bundle/blob/master/README.md">README.md</a>
    */
    @Deprecated
    public PrometheusBundle(Function<T, PrometheusBundleConfig> mapperFn, CollectorRegistry customRegistry){
        configMapperFn = mapperFn;
        prometheusRegistry = customRegistry;
    }

    @Override
    public void run(T t, Environment environment) throws Exception {
        PrometheusBundleConfig config = configMapperFn.apply(t);
        if (config == null){
            log.info("No configuration for prometheus was setup, initializing prometheus reporter with defaults");
            //no config was passed so use defaults by instantiating an empty config
            config = new PrometheusBundleConfig();
        }
        MetricRegistry registry = environment.metrics();

        DropwizardExports exports = buildBridge(registry, config);
        exports.register(prometheusRegistry);

        String endpoint = config.getScrapePath();
        if (!endpoint.startsWith("/")){
            endpoint = "/" + endpoint;
        }
        MetricsServlet servlet = new MetricsServlet(prometheusRegistry);
        //by default add metrics to the default servlet
        log.info("Adding prometheus scraping endpoing as a servlet mapped to: {}", endpoint);
        environment.admin().addServlet("prometheusreporter", servlet).addMapping(endpoint);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }


    private DropwizardExports buildBridge(MetricRegistry registry, PrometheusBundleConfig config){
        SampleBuilder sampleBuilder = null;
        if (!config.getMapperConfig().isEmpty() ){
            List<MapperConfig> mapperConfigs = config.getMapperConfig().stream()
                    .filter(mconfig -> mconfig.getMatchField() != null && !mconfig.getMatchField().isEmpty())
                    .map(mconfig ->
                    {
                        MapperConfig mapperConfig = new MapperConfig();
                        mapperConfig.setMatch(mconfig.getMatchField());
                        mapperConfig.setName(mconfig.getMatchName());
                        mapperConfig.setLabels(mconfig.getLabels());
                        return mapperConfig;
                    }
            ).collect(Collectors.toList());
            if (log.isInfoEnabled()){
                log.info("The following prometheus metric mappers have been configured");
                mapperConfigs.stream().forEach(mapperConfig ->
                        {
                            StringBuilder labelbuilder = new StringBuilder();
                            mapperConfig.getLabels().forEach((s, s2) -> labelbuilder.append(s).append( ", ").append(s2));
                            log.info("Match Field: {}\nMatch Name: {}\nLables: {}", mapperConfig.getMatch(), mapperConfig.getName(), labelbuilder.toString() );
                        }
                );
            }
            sampleBuilder = new CustomMappingSampleBuilder(mapperConfigs);
        }else{
            sampleBuilder = new DefaultSampleBuilder();
        }

        if (config.customLabels != null && !config.customLabels.isEmpty()){
            sampleBuilder = new GlobalLabelSampleBuilderDecorator(sampleBuilder, config.customLabels);
        }
        return new DropwizardExports(registry, sampleBuilder);
    }
}
