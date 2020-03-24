package com.yunlsp.framework.ingress.integrate.zuul;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.ZuulProxyAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yunlsp.framework.ingress.integrate.zuul.ZuulPluginProperties.ZUUL_PLUGIN_PROPERTIES_PREFIX;

/**
 * {@link ZuulPluginAutoConfiguration}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
@Configuration
@EnableZuulProxy
@ConditionalOnProperty(
    prefix = ZUUL_PLUGIN_PROPERTIES_PREFIX,
    value = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties({
  ZuulPluginProperties.class,
  ZuulPluginProperties.ZuulRouterExtendedProperties.class
})
@AutoConfigureBefore(ZuulProxyAutoConfiguration.class)
public class ZuulPluginAutoConfiguration {

  @Bean
  public ZuulPropertiesBeanPostProcessor zuulPropertiesBeanPostProcessor() {
    return new ZuulPropertiesBeanPostProcessor();
  }

  @Bean
  public DefaultFallbackProvider defaultFallbackProvider() {
    return new DefaultFallbackProvider();
  }
}
