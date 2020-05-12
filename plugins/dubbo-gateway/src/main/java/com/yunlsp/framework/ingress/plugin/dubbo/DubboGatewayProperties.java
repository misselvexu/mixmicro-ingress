package com.yunlsp.framework.ingress.plugin.dubbo;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static com.yunlsp.framework.ingress.IngressProperties.INGRESS_PROPERTIES_PREFIX;
import static com.yunlsp.framework.ingress.plugin.dubbo.DubboGatewayProperties.DUBBO_CONFIG_PROPERTIES_PREFIX;

/**
 * {@link DubboGatewayProperties}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020-05-12.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = DUBBO_CONFIG_PROPERTIES_PREFIX)
public class DubboGatewayProperties implements Serializable {

  public static final String DUBBO_CONFIG_PROPERTIES_PREFIX = INGRESS_PROPERTIES_PREFIX + ".dubbo";

  private boolean enabled = true;

  /**
   * Dubbo Metadata Cache Config
   *
   * <p>
   */
  @NestedConfigurationProperty
  private DubboMetadataCacheConfig metadataCache = new DubboMetadataCacheConfig();

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DubboMetadataCacheConfig implements Serializable {

    /**
     * Specifies the maximum number of entries the cache may contain.
     *
     * <p>default: 2048
     */
    @Builder.Default private int maximumSize = 2048;

    /**
     * Specifies that each entry should be automatically removed from the cache once a fixed
     * duration * has elapsed after the entry's creation
     *
     * <p>default: 24 Hours
     */
    @Builder.Default private int expireAfterAccess = 24;

    /**
     * expireAfterAccess TimeUnit
     *
     * <p>default: {@link TimeUnit#HOURS}
     */
    @Builder.Default private TimeUnit expireAfterAccessTimeunit = TimeUnit.HOURS;
  }
}
