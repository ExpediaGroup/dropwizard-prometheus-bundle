# dropwizard-prometheus-bundle (DEPRECATED)
[![Release](https://github.com/ExpediaGroup/dropwizard-prometheus-bundle/actions/workflows/release.yml/badge.svg)](https://github.com/ExpediaGroup/dropwizard-prometheus-bundle/actions/workflows/release.yml)

**DEPRECATED: We no longer maintain dropwizard-prometheus-bundle. If you have questions or concerns, please open an
issue or fork this repository.**

This bundle allows for a quick drop-in enablement of prometheus exposition format in dropwizard applications. The bundle
allows a user to integrate and configure the prometheus dropwizard metrcis bridge and the exposition servlet into a 
dropwizard application.

## Usage

### Step 1- Add the PrometeusBundleConfig attribute to your application configuration class, add a 

```java
public class YourApplicationConfiguration extends Configuration{
    PrometeusBundleConfig prometheusConfiguration;
    
     public PrometheusBundleConfig getPrometheusBundleConfig() {
        return this.prometheusBundleConfig;
     }
}
``` 

### Step 2- Add the bundle to your application

In your application class subscribe the bundle in the initialize method using the bootstrap. 

```java
public class YourApplication extends Application<YourApplicationConfiguration>{
    
     public void initialize(Bootstrap<DropwizardtestConfiguration> bootstrap) {
         //....
         bootstrap.addBundle(new PrometheusBundle<>(YourApplicationConfiguration::getPrometheusBundleConfig));
         //....
     }
     
}
```
**Note:** the bundle constructor will take a function to extract the config, just pass your getter.

### Step 3- Set your configuration

The bundle will default to subscribing a /metrics under the admin servlet to expose the metrics. 

## Configuration

The following configuration options are supported by the bundle by adding the following to your yaml file

### Override the endpoint path

```yaml
prometheusConfig:
  scrapePath: /newPath
```

### Custom metric mapping
Since dropwizard metrics do not support labels you can add mapping actions to re-normalize names and
provide labels from the original tokenized name. Refer to the [prometheus client](https://github.com/prometheus/client_java)
guide for more information.

```yaml
prometheusConfig:
  mapperConfig:
    - matchField: "com.request.*.value"
      matchName: "ContextHandler.requests"
      labels: {"method":"${0}"}
    - matchField: "com.test.*.value.*"
      matchName: "testName.value"
      labels: {"method":"${0}", "value":"${1}"}
```

### Static tag addition
Adding static tags from the client is considered and anti pattern and should be avoided since it is the prometheus 
server in the job configuration that should do this. That said, in the advent of OpenMetrics this might be something 
that could help specially if prometheus is not the scraper. Use this at your own discretion

prometheusConfig:
    customLabels: {"mylabel":"myvalue"}
