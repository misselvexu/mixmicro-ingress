package com.yunlsp.framework.ingress.plugin.sentinel.gateway;


import com.yunlsp.framework.ingress.plugin.sentinel.gateway.scg.SentinelGatewayProperties;
import com.yunlsp.framework.ingress.plugin.sentinel.gateway.zuul.SentinelZuulProperties;

/**
 * {@link ConfigConstants}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
public final class ConfigConstants {

  /**
   * Netflix Zuul type.
   */
  public static final String APP_TYPE_ZUUL_GATEWAY = "12";

  /**
   * Spring Cloud Gateway type.
   */
  public static final String APP_TYPE_SCG_GATEWAY = "11";

  /**
   * ConfigurationProperties for {@link SentinelZuulProperties}.
   */
  public static final String ZUUL_PREFIX = "spring.cloud.sentinel.zuul";

  /**
   * ConfigurationProperties for {@link SentinelGatewayProperties}.
   */
  public static final String GATEWAY_PREFIX = "spring.cloud.sentinel.scg";

  /**
   * Response type for Spring Cloud Gateway fallback.
   */
  public static final String FALLBACK_MSG_RESPONSE = "response";

  /**
   * Redirect type for Spring Cloud Gateway fallback.
   */
  public static final String FALLBACK_REDIRECT = "redirect";

  private ConfigConstants() {
    throw new AssertionError("Must not instantiate constant utility class");
  }
}
