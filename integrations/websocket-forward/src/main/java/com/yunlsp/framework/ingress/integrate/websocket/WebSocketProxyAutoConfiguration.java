package com.yunlsp.framework.ingress.integrate.websocket;

import com.yunlsp.framework.ingress.integrate.websocket.cloud.DiscoveryRouterHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import xyz.vopen.mixmicro.kits.lang.NonNull;

import java.util.Arrays;
import java.util.Map;

import static com.yunlsp.framework.ingress.integrate.websocket.WebSocketRouterProperties.WEBSOCKET_ROUTER_PROPERTIES_PREFIX;

/**
 * {@link WebSocketProxyAutoConfiguration}
 *
 * <p>Class WebSocketProxyAutoConfiguration Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/9
 */
@Configuration
@EnableWebSocket
@ConditionalOnProperty(
    prefix = WEBSOCKET_ROUTER_PROPERTIES_PREFIX,
    name = {"enabled"},
    havingValue = "true")
@EnableConfigurationProperties({WebSocketRouterProperties.class})
public class WebSocketProxyAutoConfiguration {

  @Bean
  @ConditionalOnClass(DiscoveryClient.class)
  public AbstractWebSocketServerHandler webSocketServerHandler(WebSocketRouterProperties properties, DiscoveryClient discoveryClient) {
    return new DiscoveryRouterHandler(discoveryClient, properties);
  }

  @Bean
  public WebSocketHandlerRegistration webSocketHandlerRegistration() {
    return new WebSocketHandlerRegistration.DefaultWebSocketHandlerRegistration();
  }

  @Configuration
  @AutoConfigureAfter({WebSocketHandlerRegistration.class, AbstractWebSocketServerHandler.class})
  protected class WebsocketConfig implements WebSocketConfigurer {

    final private WebSocketRouterProperties properties;

    final private WebSocketHandlerRegistration handlerRegistration;

    final private AbstractWebSocketServerHandler defaultHandler;

    public WebsocketConfig(WebSocketRouterProperties properties, WebSocketHandlerRegistration handlerRegistration, AbstractWebSocketServerHandler defaultHandler) {
      this.properties = properties;
      this.handlerRegistration = handlerRegistration;
      this.defaultHandler = defaultHandler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry webSocketHandlerRegistry) {
      Map<String, WebSocketRouterProperties.RouterHandler> handlers = properties.getHandlers();
      if (handlers != null && !handlers.isEmpty()) {
        properties
            .getHandlers()
            .forEach(
                (id, handler) -> {
                  if (handler.isEnabled()) {
                    registryHandler(webSocketHandlerRegistry, handler);
                  }
                });
      }
    }

    /**
     * register handler
     *
     * @param registry WebSocketHandlerRegistry
     * @param handler ForwardHandler
     */
    private void registryHandler(
        WebSocketHandlerRegistry registry, WebSocketRouterProperties.RouterHandler handler) {
      org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration registration =
          getRegistration(registry, handler);
      // set allowedOrigins
      if (handler.getAllowedOrigins() == null) {
        registration.setAllowedOrigins("*");
      } else {
        registration.setAllowedOrigins(handler.getAllowedOrigins());
      }
      // set interceptors
      String[] interceptorClasses = handler.getInterceptorClasses();
      if (interceptorClasses != null) {
        HandshakeInterceptor[] interceptors = Arrays.stream(interceptorClasses).map(handlerRegistration::getInterceptor).toArray(HandshakeInterceptor[]::new);
        registration.addInterceptors(interceptors);
      }
      // set withSocketJs
      if (handler.isWithJs()) {
        registration.withSockJS();
      }
    }

    private org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration
        getRegistration(
            WebSocketHandlerRegistry registry, WebSocketRouterProperties.RouterHandler handler) {
      // set handler
      String className = handler.getHandlerClass();
      if (className == null) {
        return registry.addHandler(defaultHandler, getWsPattern(handler));
      } else {
        try {
          return registry.addHandler(
              handlerRegistration.getHandler(handler.getHandlerClass()),
              getWsPattern(handler));
        } catch (Exception e) {
          throw new IllegalArgumentException("Set websocket handler error!");
        }
      }
    }
  }

  private static String getWsPattern(WebSocketRouterProperties.RouterHandler handler) {
    return handler.getPrefix() != null ? handler.getPrefix() + handler.getUri()
        : handler.getUri();
  }
}
