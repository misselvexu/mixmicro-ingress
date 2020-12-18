package com.yunlsp.framework.ingress.integrate.websocket.v2;

import com.yunlsp.framework.ingress.integrate.websocket.v2.config.WebSocketProxyInfo;
import io.vertx.core.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.mixmicro.kits.Assert;

import java.util.ArrayList;

import static com.yunlsp.framework.ingress.integrate.websocket.v2.context.WebSocketProxyContext.context;

/**
 * {@link WebSocketProxyManager}
 *
 * <p>Class WebSocketProxyManager Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/14
 */
public class WebSocketProxyManager {

  private static final Logger log = LoggerFactory.getLogger(WebSocketProxyManager.class);

  // Data
  private WebSocketProxyClient mClient = null;
  private long mSentTextCount = 0;
  private long mConnectedCount = 0;

  /** Singleton */
  private static class SingletonHolder {
    static final WebSocketProxyManager INSTANCE = new WebSocketProxyManager();
  }

  public static WebSocketProxyManager getInstance() {
    return WebSocketProxyManager.SingletonHolder.INSTANCE;
  }

  /** Constructor */
  private WebSocketProxyManager() {
    log.info("WebSocketProxyManager()");
  }

  /** Initialize */
  public synchronized void init() {
    if (mClient == null) {
      mClient = new WebSocketProxyClient();
    } else {
      log.info("Duplicated initializing");
    }
  }

  /** Connect */
  public String connect(String name, String originPath, MultiMap originHeaders) {
    ArrayList<String> uriList = new ArrayList<>();
    WebSocketProxyInfo proxyWsInfo = context().getProxyInfo(name);
    Assert.isTrue(proxyWsInfo != null, "invalid proxy instance name .");

    proxyWsInfo
        .getTargets()
        .forEach(
            target -> {
              String uri = target + "/" + originPath;
              uriList.add(uri);
            });

    String connectionId = mClient.connect(uriList, originHeaders);
    log.info("connectionId = " + connectionId);
    mConnectedCount++;
    return connectionId;
  }

  /** Disconnect */
  public void disconnect(String connectionId) {
    mClient.disconnect(connectionId);
    mConnectedCount--;
  }

  /** Send text */
  public void sendText(String connectionId, String message) {
    mClient.sendText(connectionId, message);
    mSentTextCount++;
  }

  /** Get sent text count */
  public long getSentTextCount() {
    return mSentTextCount;
  }

  /** Get connected count */
  public long getConnectedCount() {
    return mConnectedCount;
  }
}
