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


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import lombok.Data;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TestPrometheusBundle {

    public static final DropwizardAppExtension<FakeConfig> DROPWIZARD = new DropwizardAppExtension<FakeConfig>(testApp.class, new FakeConfig());

    @Test
    public void testScrapping(){
        int port = DROPWIZARD.getAdminPort();
        Client client = DROPWIZARD.client();

        String scrape = client.target("http://localhost:"+Integer.toString(port)+"/metrics")
        .request().get(String.class);

        Assertions.assertThat(scrape)
                .isNotNull()
                .contains("# HELP", "# TYPE");


    }

    @Path("/")
    public static class testApp extends Application<FakeConfig> {

        @Override
        public void initialize(Bootstrap<FakeConfig> bootstrap){
            bootstrap.addBundle(new PrometheusBundle<>(FakeConfig::getPrometheusConfig));
        }

        @Override
        public void run(FakeConfig configuration, Environment environment) throws Exception {

        }

        @GET
        public Response generateMetrics(){
            return Response.ok().build();
        }
    }

    @Data
    public static class FakeConfig extends Configuration{
        PrometheusBundleConfig prometheusConfig = new PrometheusBundleConfig();
    }
}
