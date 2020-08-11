package com.yunlsp.framework.ingress.core.autoconfigure;

import com.yunlsp.framework.ingress.core.DefaultIngressProperties;
import com.yunlsp.framework.ingress.core.access.AccessService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * {@link DefaultIngressAutoConfiguration}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/24
 */
@EnableConfigurationProperties(DefaultIngressProperties.class)
public class DefaultIngressAutoConfiguration {

  @Bean
  public AccessService accessService(DefaultIngressProperties defaultIngressProperties) {
    return new AccessService(defaultIngressProperties);
  }

}
