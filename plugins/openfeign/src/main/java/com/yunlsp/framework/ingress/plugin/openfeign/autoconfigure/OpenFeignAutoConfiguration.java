package com.yunlsp.framework.ingress.plugin.openfeign.autoconfigure;

import com.yunlsp.framework.ingress.plugin.openfeign.OpenFeignConfigProperties;
import com.yunlsp.framework.ingress.plugin.openfeign.decoder.OpenFeignInvokeErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link OpenFeignAutoConfiguration}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/5
 */
@Configuration
@ConditionalOnClass(name = {"org.springframework.cloud.openfeign.FeignContext"})
@EnableConfigurationProperties(OpenFeignConfigProperties.class)
public class OpenFeignAutoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(OpenFeignAutoConfiguration.class);

  @Bean
  public OpenFeignInvokeErrorDecoder errorDecoder() {
    return new OpenFeignInvokeErrorDecoder();
  }

  @Bean
  public feign.Logger.Level level(OpenFeignConfigProperties properties) {
    return properties.getLevel();
  }
}
