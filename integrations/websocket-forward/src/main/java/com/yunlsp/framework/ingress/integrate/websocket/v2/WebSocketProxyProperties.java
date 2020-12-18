package com.yunlsp.framework.ingress.integrate.websocket.v2;

import com.google.common.collect.Maps;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static com.yunlsp.framework.ingress.IngressProperties.INGRESS_PROPERTIES_PREFIX;
import static com.yunlsp.framework.ingress.integrate.websocket.v2.WebSocketProxyProperties.WEBSOCKET_PROPERTIES_PREFIX;

/**
 * {@link WebSocketProxyProperties}
 *
 * <p>Class WebSocketProxyProperties Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/15
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = WEBSOCKET_PROPERTIES_PREFIX)
public class WebSocketProxyProperties implements Serializable {

  public static final String WEBSOCKET_PROPERTIES_PREFIX = INGRESS_PROPERTIES_PREFIX + ".proxy.websocket";

  private static final int DEFAULT_WEBSOCKET_PORT = 8888;

  @Builder.Default private boolean enabled = true;

  @Builder.Default private int port = DEFAULT_WEBSOCKET_PORT;

  /**
   * WebSocket Routes
   *
   * <p>
   */
  @Builder.Default private Map<String, Instance> routes = Maps.newLinkedHashMap();


  // ~~

  @Getter
  @Setter
  public static class Instance implements Serializable {

    /**
     * WebSocket Proxy Location
     *
     * <p>
     */
    private String location;

    /**
     * Real WebSocket Server Targets
     *
     * <p>
     */
    private ArrayList<String> targets;

  }
}
