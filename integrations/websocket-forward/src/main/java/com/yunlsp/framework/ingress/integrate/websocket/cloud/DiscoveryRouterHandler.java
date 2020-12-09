package com.yunlsp.framework.ingress.integrate.websocket.cloud;

import com.yunlsp.framework.ingress.integrate.websocket.AbstractWebSocketServerHandler;
import com.yunlsp.framework.ingress.integrate.websocket.WebSocketRouterProperties;
import io.netty.util.CharsetUtil;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriUtils;
import xyz.vopen.mixmicro.kits.Assert;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.yunlsp.framework.ingress.integrate.websocket.WebSocketRouterProperties.Constants.WS_SCHEME;

/**
 * {@link DiscoveryRouterHandler}
 *
 * <p>Class DiscoveryRouterHandler Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/9
 */
public class DiscoveryRouterHandler extends AbstractWebSocketServerHandler {

  private volatile boolean dirty = true;

  private final Map<String, ServiceHandler> serviceMap = new ConcurrentHashMap<>();

  private final PathMatcher matcher = new AntPathMatcher();

  private final DiscoveryClient discoveryClient;

  private final WebSocketRouterProperties properties;

  public DiscoveryRouterHandler(
      DiscoveryClient discoveryClient, WebSocketRouterProperties properties) {
    this.discoveryClient = discoveryClient;
    this.properties = properties;
  }

  @Override
  public String getRouteUrl(WebSocketSession session) {
    init();
    ServiceHandler handler = getServiceId(session.getUri().getPath());
    String address = getLoadBalanceInstance(handler);
    return getWebSocketRouteUrl(address, handler, session.getUri());
  }

  /**
   * get forward url
   *
   * @param address address
   * @param handler ForwardHandler
   * @param uri uri
   * @return forward url
   */
  private String getWebSocketRouteUrl(String address, ServiceHandler handler, URI uri) {
    String prefix = Optional.ofNullable(handler.getForwardPrefix()).orElse("/");
    String query = UriUtils.encodeQuery(uri.getQuery(), CharsetUtil.UTF_8.name());
    String url =
        UriUtils.encodePath(
            handler.getPrefix() == null
                ? uri.getPath()
                : uri.getPath().substring(handler.getPrefix().length()),
            CharsetUtil.UTF_8.name());
    if (query == null) {
      return WS_SCHEME + ":/" + prefix + address + url;
    }
    return WS_SCHEME + ":/" + prefix + address + url + "?" + query;
  }

  private ServiceHandler getServiceId(String uri) {
    return serviceMap.entrySet().stream()
        .filter(entry -> matcher.match(entry.getKey(), uri))
        .findFirst()
        .map(Map.Entry::getValue)
        .orElseThrow(() -> new IllegalStateException("Can't find matching patterns for " + uri));
  }

  private String getLoadBalanceInstance(ServiceHandler handler) {
    List<String> addresses;
    if (discoveryClient != null && handler.getServiceId() != null) {
      // if service id isn't null, get address from discoveryClient
      List<ServiceInstance> instances = discoveryClient.getInstances(handler.getServiceId());
      if (instances == null || instances.isEmpty()) {
        throw new IllegalStateException("Can't find service id for " + handler.getId());
      }
      addresses =
          instances.stream()
              .map(instance -> instance.getHost() + ":" + instance.getPort())
              .collect(Collectors.toList());
    } else if (handler.getListOfServers() != null && handler.getListOfServers().length > 0) {
      // if service-id is null, get addresses from listOfServers
      addresses = Arrays.asList(handler.getListOfServers());
    } else {
      throw new IllegalStateException("Can't find service id or listOfServers for " + handler.getId());
    }
    return addresses.get(getLoadBalanceIndex(handler.getCounter(), addresses.size()));
  }

  private void init() {
    if (dirty) {
      synchronized (this) {
        if (this.dirty) {
          properties
              .getHandlers()
              .forEach(
                  (id, handler) -> {
                    if (handler.isEnabled()) {
                      serviceMap.put(
                          getWebSocketPattern(handler),
                          new ServiceHandler(
                              id,
                              handler.getServiceId(),
                              handler.getListOfServers(),
                              handler.getPrefix(),
                              handler.getForwardPrefix()));
                    }
                  });
          this.dirty = false;
        }
      }
    }
  }

  private int getLoadBalanceIndex(AtomicInteger count, int size) {
    Assert.notNull(count);
    Assert.isTrue(size > 0);
    int index;
    while (true) {
      int current = count.get();
      int next = (current + 1) % size;
      if (count.compareAndSet(current, next)) {
        index = next;
        break;
      }
    }
    return index;
  }

  private String getWebSocketPattern(WebSocketRouterProperties.RouterHandler handler) {
    return handler.getPrefix() != null ? handler.getPrefix() + handler.getUri() : handler.getUri();
  }

  static class ServiceHandler {

    private final String id;

    private final AtomicInteger counter;

    private final String serviceId;

    private final String[] listOfServers;

    private final String prefix;

    private final String forwardPrefix;

    public ServiceHandler(
        String id, String serviceId, String[] listOfServers, String prefix, String forwardPrefix) {
      this.id = id;
      this.serviceId = serviceId;
      this.listOfServers = listOfServers;
      this.prefix = prefix;
      this.forwardPrefix = forwardPrefix;
      this.counter = new AtomicInteger();
    }

    public String getId() {
      return id;
    }

    public String getServiceId() {
      return serviceId;
    }

    public String[] getListOfServers() {
      return listOfServers;
    }

    public String getPrefix() {
      return prefix;
    }

    public String getForwardPrefix() {
      return forwardPrefix;
    }

    public AtomicInteger getCounter() {
      return counter;
    }
  }
}
