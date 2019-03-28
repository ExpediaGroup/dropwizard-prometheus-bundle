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

import java.util.Collections;
import java.util.List;
import java.util.Map;


import lombok.Data;

@Data
public class PrometheusBundleConfig {
    String scrapePath = "/metrics";
    List<MapperConfig> mapperConfig = Collections.emptyList();
    //Adding custom labels from a client is considered and anti-pattern since it is prometheus that should add such
    //labels when it scrapes... that said, there are edge use cases where global labels need to be added. Use this
    //feature sparingly
    Map<String, String> customLabels = Collections.emptyMap();

    @Data
    public static class MapperConfig{
        private String matchField = "";
        private String matchName = "";
        private Map<String, String> labels = Collections.emptyMap();

        public MapperConfig(){}
    }
}


