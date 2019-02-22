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
