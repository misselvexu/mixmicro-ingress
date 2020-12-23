package com.yunlsp.framework.ingress.integrate.scg;

import com.yunlsp.framework.ingress.integrate.scg.config.BlackList;
import com.yunlsp.framework.ingress.integrate.scg.config.RateLimitRule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

  /**
   * Yaml Reference .
   *
   * <p>
   */
  private static final AtomicReference<Yaml> reference = new AtomicReference<>();

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

  // ~~

  public static SCGRouterConfigBean load(String content) {

    if(reference.get() == null) {
      Representer representer = new Representer();
      representer.getPropertyUtils().setSkipMissingProperties(true);

      Yaml yaml = new Yaml(representer);
      reference.set(yaml);
    }

    return reference.get().loadAs(content, SCGRouterConfigBean.class);
  }
}
