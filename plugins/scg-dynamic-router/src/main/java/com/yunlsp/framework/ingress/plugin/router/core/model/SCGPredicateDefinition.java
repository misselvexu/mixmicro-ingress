package com.yunlsp.framework.ingress.plugin.router.core.model;

import lombok.*;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SCGPredicateDefinition}
 *
 * <p>Class SCGPredicateDefinition Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/19
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SCGPredicateDefinition implements Serializable {

  /** 断言对应的Name */
  private String name;

  /** 配置的断言规则 */
  private Map<String, String> args = new LinkedHashMap<>();
}
