package com.yunlsp.framework.ingress.integrate.websocket.v2.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * {@link WebSocketProxyInfo}
 *
 * <p>Class WebSocketProxyInfo Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/14
 */
@Getter
@Setter
@NoArgsConstructor
public class WebSocketProxyInfo implements Serializable {

  @Getter(AccessLevel.PRIVATE)
  @Setter(AccessLevel.PRIVATE)
  private String name;

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

  public WebSocketProxyInfo(String location, ArrayList<String> targets) {
    this.location = location;
    this.targets = targets;
  }

  public WebSocketProxyInfo(String name, String location, ArrayList<String> targets) {
    this.name = name;
    this.location = location;
    this.targets = targets;
  }

  public void fixName(String name) {
    this.name = name;
  }

  public String fixedName() {
    return this.name;
  }

}
