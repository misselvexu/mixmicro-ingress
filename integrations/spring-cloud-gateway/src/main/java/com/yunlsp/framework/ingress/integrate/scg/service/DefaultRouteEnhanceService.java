package com.yunlsp.framework.ingress.integrate.scg.service;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.yunlsp.framework.ingress.integrate.scg.CacheService;
import com.yunlsp.framework.ingress.integrate.scg.RouteEnhanceService;
import com.yunlsp.framework.ingress.integrate.scg.config.BlackList;
import com.yunlsp.framework.ingress.integrate.scg.config.RateLimitRule;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.vopen.mixmicro.components.common.ResponseEntity;

import java.net.URI;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigBeanContext.context;
import static com.yunlsp.framework.ingress.integrate.scg.SCGUtils.*;

/**
 * {@link DefaultRouteEnhanceService}
 *
 * <p>Class DefaultRouteEnhanceService Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/22
 */
@RequiredArgsConstructor
public class DefaultRouteEnhanceService implements RouteEnhanceService {

  private static final Logger log = LoggerFactory.getLogger(DefaultRouteEnhanceService.class);

  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  private final CacheService cacheService;

  /**
   * 根据黑名单规则进行过滤
   *
   * @param exchange ServerWebExchange
   * @return Mono<Void>
   */
  @Override
  public Mono<Void> filterBlackList(ServerWebExchange exchange) {

    Stopwatch stopwatch = Stopwatch.createStarted();
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();
    try {
      URI originUri = getGatewayOriginalRequestUrl(exchange);
      if (originUri != null) {
        // ?
        String requestIp = getServerHttpRequestIpAddress(request);
        String requestMethod = request.getMethodValue();
        AtomicBoolean forbid = new AtomicBoolean(false);

        Set<BlackList> blackList = Sets.newHashSet(context().getNewestConfigBean().getBlackListConfig().getBlackLists());

        doBlackListCheck(forbid, blackList, originUri, requestMethod, requestIp);

        log.info("Blacklist verification completed - {}", stopwatch.stop());
        if (forbid.get()) {
          return makeWebFluxResponse(
              response,
              MediaType.APPLICATION_JSON_VALUE,
              HttpStatus.FORBIDDEN,
              ResponseEntity.builder().code(403).message("Forbidden Explained").build());
        }
      } else {
        log.info("Request IP not obtained, no blacklist check - {}", stopwatch.stop());
      }
    } catch (Exception e) {
      log.warn("Blacklist verification failed : {} - {}", e.getMessage(), stopwatch.stop());
    }

    return null;
  }

  /**
   * 根据限流规则进行过滤
   *
   * @param exchange ServerWebExchange
   * @return Mono<Void>
   */
  @Override
  public Mono<Void> filterRateLimit(ServerWebExchange exchange) {

    Stopwatch stopwatch = Stopwatch.createStarted();
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();
    try {
      URI originUri = getGatewayOriginalRequestUrl(exchange);
      if (originUri != null) {
        String requestIp = getServerHttpRequestIpAddress(request);
        String requestMethod = request.getMethodValue();
        AtomicBoolean limit = new AtomicBoolean(false);

        // Rate limit rule
        RateLimitRule rule = null;
        for (RateLimitRule limitRule : context().getNewestConfigBean().getLimitRuleConfig().getLimitRules()) {
          if(StringUtils.startsWithIgnoreCase(originUri.getPath(), limitRule.getRequestUri())
              && Objects.equals(limitRule.getRequestMethod(), requestMethod)) {
            rule = limitRule;
            break;
          }
        }

        if(rule != null) {
          Mono<Void> result = doRateLimitCheck(limit, rule, originUri, requestIp, requestMethod, response);
          log.info("Rate limit verification completed - {}", stopwatch.stop());
          if (result != null) {
            return result;
          }
        }
      } else {
        log.info("Request IP not obtained, no rate limit filter - {}", stopwatch.stop());
      }
    } catch (Exception e) {
      log.warn("Current limit failure : {} - {}", e.getMessage(), stopwatch.stop());
    }
    return null;
  }

  private void doBlackListCheck(
      AtomicBoolean forbid,
      Set<BlackList> blackList,
      URI uri,
      String requestMethod,
      String requestIp) {
    for (BlackList b : blackList) {
      if (b.isEnabled()) {

        // check ip first
        if (Objects.equals(requestIp, b.getIp())) {
          forbid.set(true);
        }

        // check path
        if (pathMatcher.match(b.getRequestUri(), uri.getPath())) {
          if (BlackList.METHOD_ALL.equalsIgnoreCase(b.getRequestMethod()) || StringUtils.equalsIgnoreCase(requestMethod, b.getRequestMethod())) {
            if (StringUtils.isNotBlank(b.getLimitFrom()) && StringUtils.isNotBlank(b.getLimitTo())) {
              if (between(LocalTime.parse(b.getLimitFrom()), LocalTime.parse(b.getLimitTo()))) {
                forbid.set(true);
              }
            } else {
              forbid.set(true);
            }
          }
        }
      }
      if (forbid.get()) {
        break;
      }
    }
  }

  private Mono<Void> doRateLimitCheck(
      AtomicBoolean limit,
      RateLimitRule rule,
      URI uri,
      String requestIp,
      String requestMethod,
      ServerHttpResponse response) {
    boolean isRateLimitRuleHit =
        rule.isEnabled()
            && (RateLimitRule.METHOD_ALL.equalsIgnoreCase(rule.getRequestMethod())
                || StringUtils.equalsIgnoreCase(requestMethod, rule.getRequestMethod()));
    if (isRateLimitRuleHit) {
      if (StringUtils.isNotBlank(rule.getLimitFrom()) && StringUtils.isNotBlank(rule.getLimitTo())) {
        if (between(LocalTime.parse(rule.getLimitFrom()), LocalTime.parse(rule.getLimitTo()))) {
          limit.set(true);
        }
      } else {
        limit.set(true);
      }
    }

    if (limit.get()) {

      String requestUri = uri.getPath();

      int count = cacheService.getCurrentRequestCount(requestUri, requestIp);
      if (count == 0) {
        cacheService.setCurrentRequestCount(requestUri, requestIp, Long.parseLong(rule.getIntervalSec()));
      } else if (count >= Integer.parseInt(rule.getCount())) {
        return makeWebFluxResponse(
            response,
            MediaType.APPLICATION_JSON_VALUE,
            HttpStatus.TOO_MANY_REQUESTS,
            ResponseEntity.builder().code(429).message("Too Many Requests").build());
      } else {
        cacheService.incrCurrentRequestCount(requestUri, requestIp);
      }
    }
    return null;
  }

  private URI getGatewayOriginalRequestUrl(ServerWebExchange exchange) {
    LinkedHashSet<URI> uris =
        exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
    URI originUri = null;
    if (uris != null) {
      originUri = uris.stream().findFirst().orElse(null);
    }
    return originUri;
  }

  private URI getGatewayRequestUrl(ServerWebExchange exchange) {
    return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
  }

  private Route getGatewayRoute(ServerWebExchange exchange) {
    return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
  }
}
