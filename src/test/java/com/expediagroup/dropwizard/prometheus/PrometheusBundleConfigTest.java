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

import java.io.File;

import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import io.dropwizard.Configuration;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import lombok.Data;

public class PrometheusBundleConfigTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<TestConfiguration> factory =
            new YamlConfigurationFactory<>(TestConfiguration.class, validator, objectMapper, "dw");

    @Test
    public void testConfigWithNoEntry() throws Exception{
        final File yml = new File(Resources.getResource("emptyconfig.yml").toURI());
        final TestConfiguration config = factory.build(yml);
        Assertions.assertThat(config).isNotNull();
    }

    @Test
    public void testEnpdpointConfig() throws Exception{
        final File yml = new File(Resources.getResource("urloverride.yml").toURI());
        final TestConfiguration config = factory.build(yml);
        Assertions.assertThat(config)
                .isNotNull();
        Assertions.assertThat(config.getPrometheusConfig())
                .isNotNull()
                .hasFieldOrPropertyWithValue("scrapePath", "/foobar");
    }

    @Test
    public void testMapperValues() throws Exception{
        final File yml = new File(Resources.getResource("mapperconfig.yml").toURI());
        final TestConfiguration config = factory.build(yml);
        Assertions.assertThat(config)
                .isNotNull();
        Assertions.assertThat(config.getPrometheusConfig())
                .isNotNull();
        Assertions.assertThat(config.getPrometheusConfig().getMapperConfig())
                .isNotNull()
                .hasSize(2);
    }


    @Data
    public static class TestConfiguration extends Configuration{
        PrometheusBundleConfig prometheusConfig = null;
    }

}
