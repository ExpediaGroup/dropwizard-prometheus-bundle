package com.expediagroup.dropwizard.prometheus;

import java.util.Collections;
import java.util.List;
import java.util.Map;


import lombok.Data;

@Data
public class PrometheusBundleConfig {
    String scrapePath = "/metrics";
    List<MapperConfig> mapperConfig = Collections.emptyList();


    @Data
    public static class MapperConfig{
        private String matchField = "";
        private String matchName = "";
        private Map<String, String> labels = Collections.emptyMap();

        public MapperConfig(){}
    }
}


