package com.yunlsp.framework.ingress.integrate.scg.config;

import com.google.common.annotations.Beta;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * {@link RateLimitRule}
 *
 * <p>Class RateLimitRule Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/22
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Beta
public class RateLimitRule implements Serializable {

  public static final int CLOSE = 0;
  public static final int OPEN = 1;

  public static final String METHOD_ALL = "all";

  /** 请求URI */
  private String requestUri;

  /** 请求方法，如果为ALL则表示对所有方法生效 */
  @Builder.Default private String requestMethod = METHOD_ALL;

  /** 限制时间起 */
  @Builder.Default private String limitFrom = "00:00:00";

  /** 限制时间止 */
  @Builder.Default private String limitTo = "23:59:59";

  /** 次数 */
  private String count;

  /** 时间周期，单位秒 */
  private String intervalSec;

  /** 状态，0关闭，1开启 */
  private boolean enabled;

  /** 规则创建时间 */
  @Builder.Default private Date createTime = new Date();

  private Date createTimeFrom;

  private Date createTimeTo;
}
