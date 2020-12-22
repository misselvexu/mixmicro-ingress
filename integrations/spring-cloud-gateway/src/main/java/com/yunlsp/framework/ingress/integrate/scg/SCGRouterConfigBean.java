package com.yunlsp.framework.ingress.integrate.scg;

import com.yunlsp.framework.ingress.integrate.scg.config.BlackList;
import com.yunlsp.framework.ingress.integrate.scg.config.RateLimitRule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link SCGRouterConfigBean}
 *
 * <p>Class SCGPluginProperties Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/18
 */
@Getter
@Setter
public class SCGRouterConfigBean {

  private boolean enabled = true;

  /**
   * Black List Defined .
   *
   * <p>
   */
  @NestedConfigurationProperty
  private BlackListProperties blackListConfig = new BlackListProperties();

  /**
   * Rate Limit Rule Defined .
   *
   * <p>
   */
  @NestedConfigurationProperty
  private RateLimitRuleProperties limitRuleConfig = new RateLimitRuleProperties();

  // ~~

  public enum Type {
    FILE,
    NACOS
  }

  @Getter
  @Setter
  public static class BlackListProperties implements Serializable {

    private boolean enabled = true;

    /**
     * Black List Defined .
     *
     * <p>
     */
    private List<BlackList> blackLists = new ArrayList<>();
  }

  @Getter
  @Setter
  public static class RateLimitRuleProperties implements Serializable {

    private boolean enabled = false;

    /**
     * Rate Limit Rule Defined .
     *
     * <p>
     */
    private List<RateLimitRule> limitRules = new ArrayList<>();
  }
}
