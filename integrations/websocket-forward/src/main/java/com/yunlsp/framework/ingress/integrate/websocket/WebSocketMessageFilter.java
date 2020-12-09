package com.yunlsp.framework.ingress.integrate.websocket;

import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;

/**
 * {@link WebSocketMessageFilter}
 *
 * <p>Class WebSocketMessageFilter Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/9
 */
public interface WebSocketMessageFilter {

  /**
   * convert from message if null,not forward
   *
   * @param fromMessage from message
   * @return forward message
   */
  Object fromMessage(AbstractWebSocketMessage fromMessage);

  /**
   * convert to message if null,not foward
   *
   * @param toMessage to message
   * @return forward message
   */
  Object toMessage(WebSocketFrame toMessage);



  // ~~

  class DefaultWebSocketMessageFilter implements WebSocketMessageFilter {

    @Override
    public Object fromMessage(AbstractWebSocketMessage fromMessage) {
      if (fromMessage != null && fromMessage.getPayload() instanceof String) {
        return new TextWebSocketFrame((String) fromMessage.getPayload());
      }
      return null;
    }

    @Override
    public Object toMessage(WebSocketFrame toMessage) {
      if (toMessage != null) {
        return new TextMessage(ByteBufUtil.getBytes(toMessage.content()));
      }
      return null;
    }
  }
}
