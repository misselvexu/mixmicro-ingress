package com.yunlsp.framework.ingress.integrate.scg.filter;

import com.yunlsp.framework.ingress.IngressConstants;
import com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigBean;
import com.yunlsp.framework.ingress.integrate.scg.RouteEnhanceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Base64Utils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigBeanContext.context;

/**
 * {@link SCGRequestFilter}
 *
 * <p>Class SCGRequestFilter Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/22
 */
@Order(0)
@RequiredArgsConstructor
public class SCGRequestFilter implements GlobalFilter {

  private final Logger log = LoggerFactory.getLogger(SCGRequestFilter.class);

  private final RouteEnhanceService routeEnhanceService;

  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  /**
   * Process the Web request and (optionally) delegate to the next {@code WebFilter} through the
   * given {@link GatewayFilterChain}.
   *
   * @param exchange the current server exchange
   * @param chain provides a way to delegate to the next filter
   * @return {@code Mono<Void>} to indicate when request processing is complete
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    SCGRouterConfigBean properties = context().getNewestConfigBean();

    if (properties.isEnabled()) {

      if (properties.getBlackListConfig().isEnabled()) {
        return routeEnhanceService.filterBlackList(exchange);
      }

      if (properties.getLimitRuleConfig().isEnabled()) {
        return routeEnhanceService.filterRateLimit(exchange);
      }
    }

    byte[] token = Base64Utils.encode((IngressConstants.GATEWAY_TOKEN_VALUE).getBytes());
    String[] headerValues = {new String(token)};
    ServerHttpRequest build =
        exchange
            .getRequest()
            .mutate()
            .header(IngressConstants.GATEWAY_TOKEN_HEADER, headerValues)
            .build();

    ServerWebExchange newExchange = exchange.mutate().request(build).build();
    return chain.filter(newExchange);
  }
}
