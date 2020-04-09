package com.yunlsp.framework.ingress.plugin.openfeign.autoconfigure;

import com.yunlsp.framework.ingress.plugin.openfeign.OpenFeignConfigProperties;
import com.yunlsp.framework.ingress.plugin.openfeign.decoder.OpenFeignInvokeErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yunlsp.framework.ingress.plugin.openfeign.OpenFeignConfigProperties.OPENFEIGN_PROPERTIES_PREFIX;

/**
 * {@link OpenFeignAutoConfiguration}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/5
 */
@Configuration
@ConditionalOnClass(name = {"org.springframework.cloud.openfeign.FeignContext"})
@ConditionalOnProperty(
    prefix = OPENFEIGN_PROPERTIES_PREFIX,
    value = "enabled",
    havingValue = "true")
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
