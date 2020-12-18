package com.yunlsp.framework.ingress.integrate.websocket.v2.context;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunlsp.framework.ingress.integrate.websocket.v2.WebSocketProxyProperties;
import com.yunlsp.framework.ingress.integrate.websocket.v2.config.WebSocketProxyInfo;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.mixmicro.kits.lang.Nullable;

import java.util.ArrayList;
import java.util.Map;

/**
 * {@link WebSocketProxyContext}
 *
 * <p>Class WebSocketProxyContext Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/15
 */
public class WebSocketProxyContext {

  private static final Logger log = LoggerFactory.getLogger(WebSocketProxyContext.class);

  public static final String WEBSOCKET_BEAN_NAME = "webSocketProxyContext";

  @Getter private WebSocketProxyProperties proxyProperties;

  private final Map<String, WebSocketProxyInfo> cachedProxies = Maps.newHashMap();

  private WebSocketProxyContext() {}

  public ArrayList<WebSocketProxyInfo> getProxyList() {
    ArrayList<WebSocketProxyInfo> result = Lists.newArrayList();

    proxyProperties
        .getRoutes()
        .forEach(
            (name, instance) ->
                result.add(
                    new WebSocketProxyInfo(name, instance.getLocation(), instance.getTargets())));

    return result;
  }

  private static class InstanceHolder {
    private static final WebSocketProxyContext INSTANCE = new WebSocketProxyContext();
  }

  public static WebSocketProxyContext context() {
    return InstanceHolder.INSTANCE;
  }

  /**
   * Register {@link WebSocketProxyProperties} bean
   *
   * @param proxyProperties instance of {@link WebSocketProxyProperties}
   */
  public void registerWebSocketProxyProperties(WebSocketProxyProperties proxyProperties) {
    this.proxyProperties = proxyProperties;
    this.proxyProperties
        .getRoutes()
        .forEach(
            (s, instance) ->
                cachedProxies.put(
                    s, new WebSocketProxyInfo(s, instance.getLocation(), instance.getTargets())));
  }

  @Nullable
  public WebSocketProxyInfo getProxyInfo(String name) {
    return cachedProxies.get(name);
  }

}
