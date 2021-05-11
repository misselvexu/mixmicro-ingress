package com.yunlsp.framework.ingress.plugin.router;

import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;

/**
 * {@link ZuulDynamicRouterAutoConfiguration}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/24
 */
public class ZuulDynamicRouterAutoConfiguration {

  @Bean
  public ZuulDynamicRouterListener dynamicRouterListener(RouteLocator routeLocator) {
    return new ZuulDynamicRouterListener(routeLocator);
  }
}
