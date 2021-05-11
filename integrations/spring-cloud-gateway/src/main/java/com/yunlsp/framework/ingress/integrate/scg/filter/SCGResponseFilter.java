package com.yunlsp.framework.ingress.integrate.scg.filter;

import com.google.common.collect.Lists;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientResponse;

import java.util.ArrayList;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CLIENT_RESPONSE_ATTR;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

/**
 * {@link SCGResponseFilter}
 *
 * <p>Class SCGResponseFilter Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/23
 */
public class SCGResponseFilter implements GlobalFilter, Ordered {

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

    return chain.filter(exchange).then(Mono.defer(() -> {

      // ~~ add cookie response support .
      HttpClientResponse clientResponse = exchange.getAttribute(CLIENT_RESPONSE_ATTR);
      if(clientResponse != null) {
        if(clientResponse.responseHeaders().contains(SET_COOKIE)) {
          exchange.getResponse().getHeaders().put(SET_COOKIE, Lists.newArrayList(clientResponse.responseHeaders().get(SET_COOKIE)));
        }
      }

      // ~~ process cors
      exchange.getResponse().getHeaders().entrySet().stream()
          .filter(kv -> (kv.getValue() != null && kv.getValue().size() > 1))
          .filter(kv -> (kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN) || kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)))
          .forEach(kv ->
              kv.setValue(new ArrayList<String>() {{
                add(kv.getValue().get(0));
              }}));

      return chain.filter(exchange);
    }));
  }

  /**
   * Get the order value of this object.
   *
   * <p>Higher values are interpreted as lower priority. As a consequence, the object with the
   * lowest value has the highest priority (somewhat analogous to Servlet {@code load-on-startup}
   * values).
   *
   * <p>Same order values will result in arbitrary sort positions for the affected objects.
   *
   * @return the order value
   * @see #HIGHEST_PRECEDENCE
   * @see #LOWEST_PRECEDENCE
   */
  @Override
  public int getOrder() {
    return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 2;
  }
}
