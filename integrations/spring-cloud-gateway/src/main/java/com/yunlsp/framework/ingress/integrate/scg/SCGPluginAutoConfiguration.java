package com.yunlsp.framework.ingress.integrate.scg;

import com.yunlsp.framework.ingress.integrate.scg.filter.SCGRequestFilter;
import com.yunlsp.framework.ingress.integrate.scg.service.DefaultRouteEnhanceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
