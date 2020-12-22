package com.yunlsp.framework.ingress.integrate.scg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

import static com.yunlsp.framework.ingress.IngressProperties.INGRESS_PROPERTIES_PREFIX;
import static com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigProperties.SCG_PLUGIN_PROPERTIES_PREFIX;

/**
 * {@link SCGRouterConfigProperties}
 *
 * <p>Class SCGRouterConfigProperties Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/22
 */
@Getter
@Setter
@ConfigurationProperties(prefix = SCG_PLUGIN_PROPERTIES_PREFIX)
public class SCGRouterConfigProperties {

  public static final String SCG_PLUGIN_PROPERTIES_PREFIX = INGRESS_PROPERTIES_PREFIX + ".scg.ext";

  private boolean enabled = true;

  private String serverAddr;

  @Builder.Default private String namespace = "public";

  @Builder.Default private String dataId = "ingress-router-ext.yaml";

  @Builder.Default private String dataGroup = "DEFAULT_GROUP";

  @Builder.Default private String fileExtension = "yaml";

  @Builder.Default private long defaultTimeout = 30000L;


  // ~~ processors ..

  @Autowired
  @JsonIgnore
  @Getter(AccessLevel.PRIVATE)
  @Setter(AccessLevel.PRIVATE)
  private Environment environment;

  @PostConstruct
  public void init() {
    this.overrideFromEnv();
  }

  private void overrideFromEnv() {
    if (StringUtils.isEmpty(this.getServerAddr())) {
      String serverAddr = environment.resolvePlaceholders("${spring.cloud.nacos.config.server-addr:}");
      if (StringUtils.isEmpty(serverAddr)) {
        serverAddr = environment.resolvePlaceholders("${spring.cloud.nacos.server-addr:localhost:8848}");
      }
      this.setServerAddr(serverAddr);
    }

    if (StringUtils.isEmpty(this.getNamespace())) {
      this.setNamespace(environment.resolvePlaceholders("${spring.cloud.nacos.config.namespace:}"));
    }
  }

}
