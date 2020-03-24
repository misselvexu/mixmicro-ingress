package com.yunlsp.framework.ingress.plugin.router;

import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;

/**
 * {@link DynamicRouterAutoConfiguration}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/24
 */
public class DynamicRouterAutoConfiguration {

  @Bean
  public DynamicRouterListener dynamicRouterListener(RouteLocator routeLocator) {
    return new DynamicRouterListener(routeLocator);
  }
}
