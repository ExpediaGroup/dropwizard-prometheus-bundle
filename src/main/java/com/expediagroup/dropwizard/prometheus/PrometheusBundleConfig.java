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


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class PrometheusBundleConfig {

    private static final String DEFAULT_SCRAPE_PATH = "/metrics";

    /**
     * Path where metrics will be exposed on the admin context
     */
    private final String scrapePath;

    /**
     * List of mapper configurations to applied to metric names
     */
    List<MapperConfig> mapperConfig = Collections.emptyList();

    /**
     * Adding custom labels from a client is considered an anti-pattern since it is prometheus that should add such labels
     * when it scrapes. That said, there are edge use cases where global lables need to be added. Use this feature sparingly.
     */
    Map<String, String> customLabels = Collections.emptyMap();

    public PrometheusBundleConfig() {
        this.scrapePath = DEFAULT_SCRAPE_PATH;
    }

    public PrometheusBundleConfig(String scrapePath) {
        this.scrapePath = scrapePath;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class MapperConfig {

        private String matchField = "";
        private String matchName = "";
        private Map<String, String> labels = Collections.emptyMap();

    }

}


