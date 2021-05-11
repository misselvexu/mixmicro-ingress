package com.yunlsp.framework.ingress.plugin.swagger.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

/**
 * {@link SCGDocumentFilter}
 *
 * <p>Class SCGDocumentFilterFactory Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/24
 */
public class SCGDocumentFilter extends AbstractGatewayFilterFactory<Object> {

  private static final String HEADER_NAME = "X-Forwarded-Prefix";
  private static final String URI = "/v2/api-docs";

  @Override
  public GatewayFilter apply(Object config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      String path = request.getURI().getPath();
      if (!StringUtils.endsWithIgnoreCase(path, URI)) {
        return chain.filter(exchange);
      }
      String basePath = path.substring(0, path.lastIndexOf(URI));
      ServerHttpRequest newRequest = request.mutate().header(HEADER_NAME, basePath).build();
      ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
      return chain.filter(newExchange);
    };
  }
}
