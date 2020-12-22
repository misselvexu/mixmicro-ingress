package com.yunlsp.framework.ingress.integrate.scg.config;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * {@link BlackList}
 *
 * <p>Class BlackList Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/21
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlackList implements Serializable {

  public static final String METHOD_ALL = "all";

  /** 黑名单ip */
  private String ip;

  /** 请求URI */
  private String requestUri;

  /** 请求方法，如果为ALL则表示对所有方法生效 */
  @Builder.Default private String requestMethod = METHOD_ALL;

  /** 限制时间起 */
  @Builder.Default private String limitFrom = "00:00:00";

  /** 限制时间止 */
  @Builder.Default private String limitTo = "23:59:59";

  /** 状态，0关闭，1开启 */
  @Builder.Default private boolean enabled = true;

  /** 规则创建时间 */
  private Date createTime = new Date();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlackList blackList = (BlackList) o;
    return enabled == blackList.enabled
        && Objects.equals(ip, blackList.ip)
        && Objects.equals(requestUri, blackList.requestUri)
        && Objects.equals(requestMethod, blackList.requestMethod)
        && Objects.equals(limitFrom, blackList.limitFrom)
        && Objects.equals(limitTo, blackList.limitTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ip, requestUri, requestMethod, limitFrom, limitTo, enabled);
  }
}
