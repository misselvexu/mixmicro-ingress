package com.yunlsp.framework.ingress.integrate.scg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.yunlsp.framework.ingress.IngressProperties.INGRESS_PROPERTIES_PREFIX;
import static com.yunlsp.framework.ingress.integrate.scg.SCGPluginProperties.SCG_PLUGIN_PROPERTIES_PREFIX;

/**
 * {@link SCGPluginProperties}
 *
 * <p>Class SCGPluginProperties Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/18
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@ConfigurationProperties(prefix = SCG_PLUGIN_PROPERTIES_PREFIX)
public class SCGPluginProperties {

  public static final String SCG_PLUGIN_PROPERTIES_PREFIX = INGRESS_PROPERTIES_PREFIX + ".scg";



}
