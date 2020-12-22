package com.yunlsp.framework.ingress.integrate.scg;

import com.yunlsp.framework.ingress.integrate.scg.filter.SCGRequestFilter;
import com.yunlsp.framework.ingress.integrate.scg.service.DefaultRouteEnhanceService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
