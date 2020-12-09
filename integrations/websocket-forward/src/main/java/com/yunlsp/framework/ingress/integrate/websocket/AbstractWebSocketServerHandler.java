package com.yunlsp.framework.ingress.integrate.websocket;

import io.netty.channel.Channel;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import xyz.vopen.mixmicro.kits.lang.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link AbstractWebSocketServerHandler}
 *
 * <p>Class AbstractWebSocketServerHandler Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/9
 */
public abstract class AbstractWebSocketServerHandler extends AbstractWebSocketHandler {

  private static final Map<String, Channel> CHANNELS = new ConcurrentHashMap<>(100);

  private WebSocketMessageFilter messageFilter = new WebSocketMessageFilter.DefaultWebSocketMessageFilter();

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message)
      throws Exception {
    Optional.ofNullable(messageFilter.fromMessage(message))
        .ifPresent(msg -> CHANNELS.get(session.getId()).writeAndFlush(msg));
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    WebSocketRouterProxyClient client = WebSocketRouterProxyClient.create(getRouteUrl(session), session, messageFilter);
    client.connect();
    Channel channel = client.getChannel();
    CHANNELS.put(session.getId(), channel);
  }

  @Override
  public void handleTransportError(WebSocketSession session, @NonNull Throwable exception)
      throws Exception {
    if (session.isOpen()) {
      session.close();
    }
    closeGracefully(session);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
    closeGracefully(session);
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

  public abstract String getRouteUrl(WebSocketSession session);

  public WebSocketMessageFilter getMessageFilter() {
    return messageFilter;
  }

  public void setMessageFilter(WebSocketMessageFilter messageFilter) {
    this.messageFilter = messageFilter;
  }

  private void closeGracefully(WebSocketSession session) {
    Optional.ofNullable(CHANNELS.get(session.getId()))
        .ifPresent(channel -> {
          try {
            if (channel.isOpen()) {
              channel.close();
            }
          } catch (Exception ignored) {
          }
        });
    CHANNELS.remove(session.getId());
  }

}
