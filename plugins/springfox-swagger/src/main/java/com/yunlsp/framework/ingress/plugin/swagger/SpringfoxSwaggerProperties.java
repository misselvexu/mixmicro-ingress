package com.yunlsp.framework.ingress.plugin.swagger;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import springfox.documentation.service.Contact;

import static com.yunlsp.framework.ingress.IngressProperties.INGRESS_PROPERTIES_PREFIX;

/**
 * {@link SpringfoxSwaggerProperties}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = INGRESS_PROPERTIES_PREFIX, ignoreInvalidFields = true)
public class SpringfoxSwaggerProperties {

  private boolean enabled = false;

  /**
   * Api Title Defined
   *
   * <p>
   */
  private String title;

  /**
   * Api Description Defined
   *
   * <p>
   */
  private String description;

  /**
   * Api {@link Contact} Defined
   *
   * <p>
   */
  @NestedConfigurationProperty private Contact contact;

  /**
   * Swagger Api Version
   *
   * <p>
   */
  private String version = "NaN";
}
