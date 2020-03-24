package com.yunlsp.framework.ingress;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * {@link IngressProperties}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
@Getter
@Setter
public class IngressProperties implements Serializable {

  public static final String INGRESS_PROPERTIES_PREFIX = "mixmicro.ingress";

  private boolean enabled = true;

  private boolean ssl;
}
