package com.yunlsp.framework.ingress.integrate.websocket;

import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link WebSocketHandlerRegistration}
 *
 * <p>Class WebSocketHandlerRegistration Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/9
 */
public interface WebSocketHandlerRegistration {

  /**
   * add AbstractWebSocketServerHandler
   *
   * @param handlers AbstractWebSocketServerHandler
   */
  void addHandler(AbstractWebSocketServerHandler... handlers);

  /**
   * add HandshakeInterceptor
   *
   * @param interceptors HandshakeInterceptor
   */
  void addInterceptor(HandshakeInterceptor... interceptors);

  /**
   * get AbstractWebSocketServerHandler
   *
   * @param handlerClassName handler name
   * @return AbstractWebSocketServerHandler
   */
  AbstractWebSocketServerHandler getHandler(String handlerClassName);

  /**
   * get HandshakeInterceptor
   *
   * @param interceptorClassName interceptor name
   * @return HandshakeInterceptor
   */
  HandshakeInterceptor getInterceptor(String interceptorClassName);


  // ~~ default implements .

  class DefaultWebSocketHandlerRegistration implements WebSocketHandlerRegistration {

    private final Map<String, AbstractWebSocketServerHandler> handlerMap = Collections.synchronizedMap(new HashMap<>(5));

    private final Map<String, HandshakeInterceptor> interceptorMap = Collections.synchronizedMap(new HashMap<>(5));

    @Override
    public void addHandler(AbstractWebSocketServerHandler... handlers) {
      Arrays.stream(handlers)
          .forEach(handler -> handlerMap.putIfAbsent(handler.getClass().getName(), handler));
    }

    @Override
    public void addInterceptor(HandshakeInterceptor... interceptors) {
      Arrays.stream(interceptors)
          .forEach(
              interceptor ->
                  interceptorMap.putIfAbsent(interceptor.getClass().getName(), interceptor));
    }

    @Override
    public AbstractWebSocketServerHandler getHandler(String handlerClassName) {
      return handlerMap.get(handlerClassName);
    }

    @Override
    public HandshakeInterceptor getInterceptor(String interceptorClassName) {
      return interceptorMap.get(interceptorClassName);
    }
  }
}
