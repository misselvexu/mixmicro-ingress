package com.yunlsp.framework.ingress.plugin.sentinel.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link IngressEnvironmentPostProcessor}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
public class IngressEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final String SENTINEL_FILTER_ENABLED = "spring.cloud.sentinel.filter.enabled";

  private static final String PROPERTY_SOURCE_NAME = "defaultProperties";

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication springApplication) {
    addDefaultPropertySource(environment);
  }

  private void addDefaultPropertySource(ConfigurableEnvironment environment) {

    Map<String, Object> map = new HashMap<>();

    configureDefaultProperties(map);

    addOrReplace(environment.getPropertySources(), map);
  }

  private void configureDefaultProperties(Map<String, Object> source) {
    // Required Properties
    source.put(SENTINEL_FILTER_ENABLED, "false");
  }

  private void addOrReplace(MutablePropertySources propertySources, Map<String, Object> map) {
    MapPropertySource target = null;
    if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
      PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
      if (source instanceof MapPropertySource) {
        target = (MapPropertySource) source;
        for (String key : map.keySet()) {
          if (!target.containsProperty(key)) {
            target.getSource().put(key, map.get(key));
          }
        }
      }
    }
    if (target == null) {
      target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
    }
    if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
      propertySources.addLast(target);
    }
  }
}
