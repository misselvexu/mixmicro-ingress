package com.yunlsp.framework.ingress.plugin.router.core.model;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link SCGRouteDefinition}
 *
 * <p>Class SCGRouteDefinition Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/19
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SCGRouteDefinition implements Serializable {

  /** 路由的Id */
  private String id;

  /** 路由断言集合配置 */
  private List<SCGPredicateDefinition> predicates = new ArrayList<>();

  /** 路由过滤器集合配置 */
  private List<SCGFilterDefinition> filters = new ArrayList<>();

  /** 路由规则转发的目标uri */
  private String uri;

  /** 路由执行的顺序 */
  private int order = 0;
}
