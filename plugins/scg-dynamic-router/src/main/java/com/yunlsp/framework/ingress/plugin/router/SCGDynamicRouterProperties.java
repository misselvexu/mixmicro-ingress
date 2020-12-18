package com.yunlsp.framework.ingress.plugin.router;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;

import static com.yunlsp.framework.ingress.IngressProperties.INGRESS_PROPERTIES_PREFIX;
import static com.yunlsp.framework.ingress.plugin.router.SCGDynamicRouterProperties.SCG_DYNAMIC_PROPERTIES_PREFIX;

/**
 * {@link SCGDynamicRouterProperties}
 *
 * <p>Class SCGDynamicRouterProperties Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/18
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = SCG_DYNAMIC_PROPERTIES_PREFIX)
public class SCGDynamicRouterProperties implements Serializable {

  public static final String SCG_DYNAMIC_PROPERTIES_PREFIX = INGRESS_PROPERTIES_PREFIX + ".scg.dynamic.router";

  @Builder.Default private boolean enabled = true;

  private String serverAddr;

  @Builder.Default private String namespace = "public";

  @Builder.Default private String dataId = "ingress-dynamic-router.json";

  @Builder.Default private String dataGroup = "DEFAULT_GROUP";

  @Builder.Default private String fileExtension = "json";

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
