package com.yunlsp.framework.ingress.plugin.swagger;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static com.yunlsp.framework.ingress.IngressProperties.INGRESS_PROPERTIES_PREFIX;
import static com.yunlsp.framework.ingress.plugin.swagger.SCGDocumentProperties.SCG_DOCUMENT_PROPERTIES_PREFIX;

/**
 * {@link SCGDocumentProperties}
 *
 * <p>Class SCGDocumentProperties Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/24
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = SCG_DOCUMENT_PROPERTIES_PREFIX)
public class SCGDocumentProperties implements Serializable {

  public static final String SCG_DOCUMENT_PROPERTIES_PREFIX = INGRESS_PROPERTIES_PREFIX + ".doc";

  private Boolean enabled = false;

  /**
   * 需要暴露doc的服务列表，多个值时用逗号分隔
   *
   * <p>
   */
  private String resources;
}
