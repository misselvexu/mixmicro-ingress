package com.yunlsp.framework.ingress.plugin.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.Lists;
import com.yunlsp.framework.ingress.integrate.zuul.ZuulPluginProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * {@link SpringfoxSwaggerResourceChangeListener}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
public class SpringfoxSwaggerResourceChangeListener
    implements SwaggerResourcesProvider, ApplicationListener<ApplicationEvent>, EnvironmentAware {

  private static final Logger log = LoggerFactory.getLogger(SpringfoxSwaggerResourceChangeListener.class);

  private static final String SERVICE_INSTANCE_SWAGGER_DOCS_URI = "%s/v2/api-docs";

  private static volatile List<SwaggerResource> resources = Lists.newArrayList();

  private Environment environment;

  private final ZuulPluginProperties.ZuulRouterExtendedProperties zuulRouterExtendedProperties;

  private static ObjectMapper mapper = new JsonMapper();

  /**
   * Constructor with instance of {@link RouteLocator}
   *
   * @param zuulRouterExtendedProperties zuul route locator
   */
  public SpringfoxSwaggerResourceChangeListener(
      ZuulPluginProperties.ZuulRouterExtendedProperties zuulRouterExtendedProperties) {
    this.zuulRouterExtendedProperties = zuulRouterExtendedProperties;
  }

  /**
   * Retrieves an instance of the appropriate type. The returned object may or may not be a new
   * instance, depending on the implementation.
   *
   * @return an instance of the appropriate type
   */
  @Override
  public List<SwaggerResource> get() {
    return resources;
  }

  /**
   * Handle an application event.
   *
   * @param event the event to respond to
   */
  @Override
  public void onApplicationEvent(@NonNull ApplicationEvent event) {
    if (event instanceof ApplicationReadyEvent || event instanceof RoutesRefreshedEvent) {
      long starting = System.currentTimeMillis();
      //
      try {
        refresh0();
      } catch (Exception e) {
        log.error("[==SWAGGER==] resource refresh happened exception", e);
      }
      //
      if(log.isDebugEnabled()) {
        log.debug("[==SWAGGER==] resource is refreshed. cost: {} ms" , (System.currentTimeMillis() - starting));
      }
    }
  }

  private void refresh0() throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("[==SWAGGER==] before resource refresh, resources: {} " , mapper.writeValueAsString(resources));
    }
    resources.clear();
    Map<String, ZuulPluginProperties.ZuulRouterExtendedProperties.ZuulExtendedRoute> routes = zuulRouterExtendedProperties.getRoutes();

    routes.forEach(this::process);
    //
    if(log.isDebugEnabled()) {
      log.debug("[==SWAGGER==] after resource refreshed , resources: {} " , mapper.writeValueAsString(resources));
    }
  }

  private void process(String key, ZuulPluginProperties.ZuulRouterExtendedProperties.ZuulExtendedRoute route) {

    if(!route.getSwagger().isEnabled()) {
      return;
    }

    String prefix = route.getPath().replace("/**","").replace("/*","");

    String location = String.format(SERVICE_INSTANCE_SWAGGER_DOCS_URI, prefix);

    SwaggerResource resource = build(route.getLocation(), location, Optional.ofNullable(route.getVersion()).orElse("NaN"));
    resources.add(resource);
  }

  /**
   * Return the property value associated with the given key, or {@code defaultValue} if the key
   * cannot be resolved.
   *
   * @param key the property name to resolve
   * @param targetType the expected type of the property value
   * @param defaultValue the default value to return if no value is found
   * @see Environment#getRequiredProperty(String, Class)
   */
  private <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
    if (StringUtils.isEmpty(key)) {
      return this.environment.getProperty(key, targetType, defaultValue);
    }

    // DEFAULT VALUE
    return defaultValue;
  }

  private SwaggerResource build(String name, String location, String version) {
    SwaggerResource resource = new SwaggerResource();
    resource.setName(name);
    resource.setLocation(location);
    resource.setSwaggerVersion(version);
    return resource;
  }

  /**
   * Set the {@code Environment} that this component runs in.
   *
   * @param environment application env
   */
  @Override
  public void setEnvironment(@NonNull Environment environment) {
    this.environment = environment;
  }
}
