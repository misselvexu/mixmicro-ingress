package com.yunlsp.framework.ingress.plugin.dubbo;

import com.alibaba.cloud.dubbo.metadata.DubboRestServiceMetadata;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yunlsp.framework.ingress.plugin.dubbo.DubboGatewayProperties.DUBBO_CONFIG_PROPERTIES_PREFIX;

/**
 * {@link DubboGatewayAutoConfiguration}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020-05-12.
 */
@Configuration
@EnableConfigurationProperties(DubboGatewayProperties.class)
@ConditionalOnProperty(
    prefix = DUBBO_CONFIG_PROPERTIES_PREFIX,
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@ServletComponentScan
public class DubboGatewayAutoConfiguration {

  @Bean
  Cache<DubboRestServiceMetadata, GenericService> dubboRestServiceMetadataCache(
      DubboGatewayProperties properties) {
    return CacheBuilder.newBuilder()
        .recordStats()
        .maximumSize(
            properties.getMetadataCache().getMaximumSize()) // refactor with context properties
        .expireAfterAccess(
            properties.getMetadataCache().getExpireAfterAccess(),
            properties
                .getMetadataCache()
                .getExpireAfterAccessTimeunit()) // refactor with context properties
        .build();
  }
}
