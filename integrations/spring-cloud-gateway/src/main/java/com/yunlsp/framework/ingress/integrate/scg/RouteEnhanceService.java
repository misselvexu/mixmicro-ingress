package com.yunlsp.framework.ingress.integrate.scg;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * {@link RouteEnhanceService}
 *
 * <p>Class RouteEnhanceService Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/22
 */
public interface RouteEnhanceService {

  /**
   * 根据黑名单规则进行过滤
   *
   * @param exchange ServerWebExchange
   * @return Mono<Void>
   */
  Mono<Void> filterBlackList(ServerWebExchange exchange);

  /**
   * 根据限流规则进行过滤
   *
   * @param exchange ServerWebExchange
   * @return Mono<Void>
   */
  Mono<Void> filterRateLimit(ServerWebExchange exchange);
}
