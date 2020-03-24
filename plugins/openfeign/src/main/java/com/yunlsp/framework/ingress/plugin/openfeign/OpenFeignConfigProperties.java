package com.yunlsp.framework.ingress.plugin.openfeign;

import feign.Logger;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static com.yunlsp.framework.ingress.plugin.openfeign.OpenFeignConfigProperties.OPENFEIGN_PROPERTIES_PREFIX;

/**
 * {@link OpenFeignConfigProperties}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/5
 */
@Getter
@Setter
@ConfigurationProperties(prefix = OPENFEIGN_PROPERTIES_PREFIX)
public class OpenFeignConfigProperties implements Serializable {

  public static final String OPENFEIGN_PROPERTIES_PREFIX = "mixmicro.feign";

  private Logger.Level level = Logger.Level.FULL;
}
