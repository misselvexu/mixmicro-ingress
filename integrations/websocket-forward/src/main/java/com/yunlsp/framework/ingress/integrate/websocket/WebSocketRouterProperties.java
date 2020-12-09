package com.yunlsp.framework.ingress.integrate.websocket;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.yunlsp.framework.ingress.IngressProperties.INGRESS_PROPERTIES_PREFIX;
import static com.yunlsp.framework.ingress.integrate.websocket.WebSocketRouterProperties.WEBSOCKET_ROUTER_PROPERTIES_PREFIX;

/**
 * {@link WebSocketRouterProperties}
 *
 * <p>Class WebSocketRouterProperties Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/9
 */
@ConfigurationProperties(prefix = WEBSOCKET_ROUTER_PROPERTIES_PREFIX)
public class WebSocketRouterProperties implements Serializable {

  public static final String WEBSOCKET_ROUTER_PROPERTIES_PREFIX = INGRESS_PROPERTIES_PREFIX + ".websockets";

  private static final String URL_START_PREFIX = "/";

  private boolean enabled = true;

  private Map<String, RouterHandler> handlers = new HashMap<>(5);

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Map<String, RouterHandler> getHandlers() {
    return handlers;
  }

  public void setHandlers(Map<String, RouterHandler> handlers) {
    this.handlers = handlers;
  }

  @PostConstruct
  public void init() {
    handlers.forEach(
        (key, handler) -> {
          if (!StringUtils.hasText(handler.getId())) {
            handler.id = key;
          }
        });
  }

  public static class Constants {

    public static final String WS_SCHEME = "ws";

    public static final String WSS_SCHEME = "wss";

  }

  public static class RouterHandler {

    /** enabled */
    private boolean enabled = true;

    /** id */
    private String id;

    /** route prefix */
    private String prefix;

    /** forward prefix */
    private String forwardPrefix;

    /** websocket handler uri */
    private String uri;

    /** service id,used for finding service address from DiscoveryClient */
    private String serviceId;

    /** withJs */
    private boolean withJs;

    /** list of servers */
    private String[] listOfServers;

    /** allowedOrigins */
    private String[] allowedOrigins;

    /** bean name of AbstractWsServerHandler if null,use global ForwardHandler. */
    private String handlerClass;

    /** bean name array of HandshakeInterceptor. if null,use global HandshakeInterceptor. */
    private String[] interceptorClasses;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getPrefix() {
      if (prefix == null || prefix.startsWith(URL_START_PREFIX)) {
        return prefix;
      }
      return URL_START_PREFIX + prefix;
    }

    public void setPrefix(String prefix) {
      this.prefix = prefix;
    }

    public String getForwardPrefix() {
      if (forwardPrefix == null || forwardPrefix.startsWith(URL_START_PREFIX)) {
        return forwardPrefix;
      }
      return URL_START_PREFIX + forwardPrefix;
    }

    public void setForwardPrefix(String forwardPrefix) {
      this.forwardPrefix = forwardPrefix;
    }

    public String getUri() {
      return uri;
    }

    public void setUri(String uri) {
      this.uri = uri;
    }

    public String getServiceId() {
      return serviceId;
    }

    public void setServiceId(String serviceId) {
      this.serviceId = serviceId;
    }

    public boolean isWithJs() {
      return withJs;
    }

    public void setWithJs(boolean withJs) {
      this.withJs = withJs;
    }

    public String[] getListOfServers() {
      return listOfServers;
    }

    public void setListOfServers(String[] listOfServers) {
      this.listOfServers = listOfServers;
    }

    public String[] getAllowedOrigins() {
      return allowedOrigins;
    }

    public void setAllowedOrigins(String[] allowedOrigins) {
      this.allowedOrigins = allowedOrigins;
    }

    public String getHandlerClass() {
      return handlerClass;
    }

    public void setHandlerClass(String handlerClass) {
      this.handlerClass = handlerClass;
    }

    public String[] getInterceptorClasses() {
      return interceptorClasses;
    }

    public void setInterceptorClasses(String[] interceptorClasses) {
      this.interceptorClasses = interceptorClasses;
    }
  }
}
