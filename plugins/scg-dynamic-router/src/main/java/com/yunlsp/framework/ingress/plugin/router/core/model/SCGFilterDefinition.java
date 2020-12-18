package com.yunlsp.framework.ingress.plugin.router.core.model;

import lombok.*;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SCGFilterDefinition}
 *
 * <p>Class SCGFilterDefinition Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/19
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SCGFilterDefinition implements Serializable {

  /** Filter Name */
  private String name;

  /** 对应的路由规则 */
  private Map<String, String> args = new LinkedHashMap<>();
}
