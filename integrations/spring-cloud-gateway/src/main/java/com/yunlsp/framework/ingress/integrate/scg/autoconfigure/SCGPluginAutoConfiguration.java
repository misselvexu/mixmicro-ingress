package com.yunlsp.framework.ingress.integrate.scg.autoconfigure;

import com.yunlsp.framework.ingress.integrate.scg.CacheService;
import com.yunlsp.framework.ingress.integrate.scg.RouteEnhanceService;
import com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigProperties;
import com.yunlsp.framework.ingress.integrate.scg.filter.SCGCorsFilter;
import com.yunlsp.framework.ingress.integrate.scg.filter.SCGRequestFilter;
import com.yunlsp.framework.ingress.integrate.scg.filter.SCGResponseFilter;
import com.yunlsp.framework.ingress.integrate.scg.listener.SCGApplicationLifecycleListener;
import com.yunlsp.framework.ingress.integrate.scg.service.DefaultRouteEnhanceService;
import com.yunlsp.framework.ingress.integrate.scg.service.SCGRouterExtConfigService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigProperties.Cors.SCG_CORS_PROPERTIES_PREFIX;
import static com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigProperties.SCG_PLUGIN_PROPERTIES_PREFIX;

/**
 * {@link SCGPluginAutoConfiguration}
 *
 * <p>Class SCGPluginAutoConfiguration Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/18
 */
@Configuration
@EnableConfigurationProperties(SCGRouterConfigProperties.class)
@ConditionalOnProperty(
    prefix = SCG_PLUGIN_PROPERTIES_PREFIX,
    value = "enabled",
    havingValue = "true")
public class SCGPluginAutoConfiguration {

  @Bean
  public CacheService cacheService() {
    // TODO :: support redis or cluster cache ..
    return new CacheService() {
      @Override
      public int getCurrentRequestCount(String uri, String ip) {
        return 0;
      }

      @Override
      public void setCurrentRequestCount(String uri, String ip, Long time) {}

      @Override
      public void incrCurrentRequestCount(String uri, String ip) {}
    };
  }

  @Bean
  public RouteEnhanceService routeEnhanceService(CacheService cacheService) {
    return new DefaultRouteEnhanceService(cacheService);
  }

  @Bean
  public SCGRequestFilter scgRequestFilter(RouteEnhanceService service) {
    return new SCGRequestFilter(service);
  }

  @Bean
  public SCGResponseFilter scgResponseFilter() {
    return new SCGResponseFilter();
  }

  @Bean
  public SCGRouterExtConfigService extConfigService(SCGRouterConfigProperties properties) {
    return new SCGRouterExtConfigService(properties);
  }

  @Bean
  public SCGApplicationLifecycleListener scgApplicationLifecycleListener() {
    return new SCGApplicationLifecycleListener();
  }

  @Configuration
  @ConditionalOnProperty(
      prefix = SCG_CORS_PROPERTIES_PREFIX,
      value = "enabled",
      havingValue = "true",
      matchIfMissing = true)
  public static class CorsConfiguration {

    @Bean
    public SCGCorsFilter scgCorsFilter(SCGRouterConfigProperties properties) {
      return new SCGCorsFilter(properties);
    }
  }
}
