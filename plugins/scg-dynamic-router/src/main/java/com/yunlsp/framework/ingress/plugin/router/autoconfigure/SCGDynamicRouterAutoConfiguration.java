package com.yunlsp.framework.ingress.plugin.router.autoconfigure;

import com.yunlsp.framework.ingress.plugin.router.SCGDynamicRouterProperties;
import com.yunlsp.framework.ingress.plugin.router.context.SCGDynamicRouterService;
import com.yunlsp.framework.ingress.plugin.router.context.listener.SCGLifecycleListener;
import com.yunlsp.framework.ingress.plugin.router.core.NacosDynamicConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yunlsp.framework.ingress.plugin.router.SCGDynamicRouterProperties.SCG_DYNAMIC_PROPERTIES_PREFIX;

/**
 * {@link SCGDynamicRouterAutoConfiguration}
 *
 * <p>Class SCGDynamicRouterAutoConfiguration Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/19
 */
@Configuration
@EnableConfigurationProperties(SCGDynamicRouterProperties.class)
@ConditionalOnProperty(
    prefix = SCG_DYNAMIC_PROPERTIES_PREFIX,
    value = "enabled",
    havingValue = "true")
public class SCGDynamicRouterAutoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(SCGDynamicRouterAutoConfiguration.class);

  @Bean
  public SCGDynamicRouterService dynamicRouterService(RouteDefinitionWriter routeDefinitionWriter) {
    return new SCGDynamicRouterService(routeDefinitionWriter);
  }

  @Bean
  public NacosDynamicConfigService nacosDynamicConfigService(
      SCGDynamicRouterService dynamicRouterService,
      SCGDynamicRouterProperties dynamicRouterProperties) {
    return new NacosDynamicConfigService(dynamicRouterService, dynamicRouterProperties);
  }

  @Bean
  public SCGLifecycleListener scgLifecycleListener() {
    return new SCGLifecycleListener();
  }


}
