package com.yunlsp.framework.ingress.integrate.scg.filter;

import com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * {@link SCGCorsFilter}
 *
 * <p>Class SCGCorsFilter Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/23
 */
public class SCGCorsFilter implements GlobalFilter, Ordered {

  private static final String ALL = "*";
  private static final String MAX_AGE = "3600L";

  private final SCGRouterConfigProperties properties;

  public SCGCorsFilter(SCGRouterConfigProperties properties) {
    this.properties = properties;
  }

  /**
   * Process the Web request and (optionally) delegate to the next {@code WebFilter}
   * through the given {@link GatewayFilterChain}.
   *
   * @param exchange the current server exchange
   * @param chain    provides a way to delegate to the next filter
   * @return {@code Mono<Void>} to indicate when request processing is complete
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    if(!properties.getCors().isEnabled()) {
      return chain.filter(exchange);
    }

    ServerHttpRequest request = exchange.getRequest();
    if (!CorsUtils.isCorsRequest(request)) {
      return chain.filter(exchange);
    }

    HttpHeaders requestHeaders = request.getHeaders();
    ServerHttpResponse response = exchange.getResponse();
    HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();
    HttpHeaders headers = response.getHeaders();
    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
    headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());
    if (requestMethod != null) {
      headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
    }
    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
    headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
    if (request.getMethod() == HttpMethod.OPTIONS) {
      response.setStatusCode(HttpStatus.OK);
      return Mono.empty();
    }
    return chain.filter(exchange);
  }

  /**
   * Get the order value of this object.
   * <p>Higher values are interpreted as lower priority. As a consequence,
   * the object with the lowest value has the highest priority (somewhat
   * analogous to Servlet {@code load-on-startup} values).
   * <p>Same order values will result in arbitrary sort positions for the
   * affected objects.
   *
   * @return the order value
   * @see #HIGHEST_PRECEDENCE
   * @see #LOWEST_PRECEDENCE
   */
  @Override
  public int getOrder() {
    return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
  }
}
