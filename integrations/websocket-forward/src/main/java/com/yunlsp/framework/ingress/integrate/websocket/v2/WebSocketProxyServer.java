package com.yunlsp.framework.ingress.integrate.websocket.v2;

import com.yunlsp.framework.ingress.integrate.websocket.v2.config.WebSocketProxyInfo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static com.yunlsp.framework.ingress.integrate.websocket.v2.context.WebSocketProxyContext.context;

/**
 * {@link WebSocketProxyServer}
 *
 * <p>Class WebSocketProxyServer Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/14
 */
public class WebSocketProxyServer extends AbstractVerticle {

  // Logger
  private static final Logger log = LoggerFactory.getLogger(WebSocketProxyServer.class);

  private final int port;

  /**
   * WebSocket Proxy Server Constructor
   *
   * <p>
   */
  public WebSocketProxyServer() {
    this.port = context().getProxyProperties().getPort();
  }

  /**
   * If your verticle does a simple, synchronous start-up then override this method and put your
   * start-up code in here.
   *
   * @throws Exception maybe thrown {@link Exception}
   */
  @Override
  public void start() throws Exception {

    /*
     * Http Server
     */
    HttpServerOptions options = new HttpServerOptions();
    options.setCompressionSupported(true);
    HttpServer httpServer = vertx.createHttpServer(options);

    httpServer.webSocketHandler(new Handler<ServerWebSocket>(){

      /**
       * Something has happened, so handle it.
       *
       * @param ws the event to handle
       */
      @Override
      public void handle(ServerWebSocket ws) {
        // Log
        if (log.isInfoEnabled()) {
          log.info("------------------------------------------------------------------------");
          log.info("WebSocket Request Headers");
          log.info("------------------------------------------------------------------------");
          log.info("PATH # " + ws.path());
          ws.headers().forEach(header -> log.info(header.getKey() + " = " + header.getValue()));
          log.info("------------------------------------------------------------------------");
        }

        // Route
        boolean tempRouteResult = false;
        String tempRouteName = null;
        String tempPath = null;
        ArrayList<WebSocketProxyInfo> channelList = context().getProxyList();
        for (final WebSocketProxyInfo channel : channelList) {
          final String routePath = makeRoutePath(channel.getLocation());
          log.info("routePath = " + routePath);
          if (ws.path().indexOf(routePath) == 0) {
            tempRouteName = channel.fixedName();
            tempPath = parsePath(ws.path(), routePath);
            tempRouteResult = true;
            break;
          }
        }

        final boolean routeResult = tempRouteResult;
        final String routeName = tempRouteName;
        final String path = tempPath;
        log.info("routeResult = " + routeResult);
        log.info("routeName = " + routeName);
        log.info("path = " + path);

        // End
        if (routeResult) {
          String connectionId = WebSocketProxyManager.getInstance().connect(routeName, path, ws.headers());
          log.info("connectionId = " + connectionId);
          handleMessage(ws, connectionId);
        } else {
          ws.reject();
        }

      }
    }).listen(port);


//    httpServer.webSocketHandler(ws -> {
//      // Log
//      if (log.isInfoEnabled()) {
//        log.info("------------------------------------------------------------------------");
//        log.info("WebSocket Request Headers");
//        log.info("------------------------------------------------------------------------");
//        log.info("PATH # " + ws.path());
//        ws.headers().forEach(header -> log.info(header.getKey() + " = " + header.getValue()));
//        log.info("------------------------------------------------------------------------");
//      }
//
//      // Route
//      boolean tempRouteResult = false;
//      String tempRouteName = null;
//      String tempPath = null;
//      ArrayList<WebSocketProxyInfo> channelList = context().getProxyList();
//      for (final WebSocketProxyInfo channel : channelList) {
//        final String routePath = makeRoutePath(channel.getLocation());
//        log.info("routePath = " + routePath);
//        if (ws.path().indexOf(routePath) == 0) {
//          tempRouteName = channel.fixedName();
//          tempPath = parsePath(ws.path(), routePath);
//          tempRouteResult = true;
//          break;
//        }
//      }
//
//      final boolean routeResult = tempRouteResult;
//      final String routeName = tempRouteName;
//      final String path = tempPath;
//      log.info("routeResult = " + routeResult);
//      log.info("routeName = " + routeName);
//      log.info("path = " + path);
//
//      // End
//      if (routeResult) {
//        String connectionId = WebSocketProxyManager.getInstance().connect(routeName, path, ws.headers());
//        log.info("connectionId = " + connectionId);
//        handleMessage(ws, connectionId);
//      } else {
//        ws.reject();
//      }
//    });
//    httpServer.listen(port);
  }

  /**
   * If your verticle has simple synchronous clean-up tasks to complete then override this method
   * and put your clean-up code in here.
   *
   * @throws Exception maybe thrown {@link Exception}
   */
  @Override
  public void stop() throws Exception {
    super.stop();
  }

  // ~~ private methods .

  /** Handle message */
  protected void handleMessage(final ServerWebSocket ws, final String connectionId) {
    ws.frameHandler(
        event -> {
          log.info("Frame Data : " + event.textData());
          WebSocketProxyManager.getInstance().sendText(connectionId, event.textData());
        });

    ws.drainHandler(event -> log.info("drainHandler()"));

    ws.endHandler(event -> log.info("endHandler()"));

    ws.closeHandler(
        event -> {
          log.info("closeHandler()");
          WebSocketProxyManager.getInstance().disconnect(connectionId);
        });

    ws.exceptionHandler(event -> log.info("exceptionHandler()", event));
  }

  /** Make route path */
  private String makeRoutePath(String location) {
    String routePath = location;
    if (routePath.length() > 1) {
      if ('/' == routePath.charAt(routePath.length() - 1)) {
        routePath = routePath.substring(0, routePath.length() - 2);
      } else if ('*' == routePath.charAt(routePath.length() - 1)) {
        routePath = routePath.substring(0, routePath.length() - 3);
      }
    }
    return routePath;
  }

  /** Parse path */
  private String parsePath(String requestPath, String routePath) {
    String result = requestPath.substring(routePath.length());
    if (!StringUtils.isEmpty(result)) {
      if (result.charAt(0) == '/') {
        result = result.substring(1);
      }
    }
    return result;
  }
}
