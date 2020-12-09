package com.yunlsp.framework.ingress.integrate.websocket.codec;

import com.yunlsp.framework.ingress.integrate.websocket.WebSocketMessageFilter;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Optional;

/**
 * {@link WebSocketRouterClientHandler}
 *
 * <p>Class WebSocketRouterClientHandler Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/9
 */
public class WebSocketRouterClientHandler extends SimpleChannelInboundHandler<Object> {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketRouterClientHandler.class);

  private final WebSocketClientHandshaker handshaker;

  /** WebSocketSession */
  private final WebSocketSession webSocketSession;

  /** MessageFilter */
  private final WebSocketMessageFilter messageFilter;

  /** ChannelPromise */
  private ChannelPromise handshakeFuture;

  public WebSocketRouterClientHandler(
      WebSocketClientHandshaker handshaker,
      WebSocketSession webSocketSession,
      WebSocketMessageFilter messageFilter) {
    this.handshaker = handshaker;
    this.webSocketSession = webSocketSession;
    this.messageFilter = messageFilter;
  }

  public ChannelFuture handshakeFuture() {
    return handshakeFuture;
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) {
    handshakeFuture = ctx.newPromise();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    handshaker.handshake(ctx.channel());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    ctx.close();
    closeWebSocketSession();
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    Channel ch = ctx.channel();
    if (!handshaker.isHandshakeComplete()) {
      try {
        handshaker.finishHandshake(ch, (FullHttpResponse) msg);
        handshakeFuture.setSuccess();
      } catch (WebSocketHandshakeException e) {
        handshakeFuture.setFailure(e);
      }
      return;
    }
    if (msg instanceof FullHttpResponse) {
      FullHttpResponse response = (FullHttpResponse) msg;
      throw new IllegalStateException("Unexpected FullHttpResponse .. ");
    }
    // handler websocket message
    if (msg instanceof CloseWebSocketFrame) {
      closeWebSocketSession();
      return;
    }
    Optional.ofNullable(msg)
        .map(frame -> messageFilter.toMessage((WebSocketFrame) frame))
        .filter(message -> message instanceof WebSocketMessage)
        .ifPresent(
            message -> {
              try {
                webSocketSession.sendMessage((WebSocketMessage<?>) message);
              } catch (IOException e) {
                throw new IllegalStateException(e);
              }
            });
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    if (!handshakeFuture.isDone()) {
      handshakeFuture.setFailure(cause);
    }
    ctx.close();
    closeWebSocketSession();
  }

  /** close websocket session */
  private void closeWebSocketSession() {
    if (webSocketSession.isOpen()) {
      try {
        webSocketSession.close();
      } catch (IOException ignored) {
      }
    }
  }
}
