package com.yunlsp.framework.ingress.integrate.scg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static com.yunlsp.framework.ingress.common.IngressSerializers.to;

/**
 * {@link SCGRouterConfigBeanContext}
 *
 * <p>Class SCGRouterConfigBeanContext Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/22
 */
public class SCGRouterConfigBeanContext {

  // ~~

  private SCGRouterConfigBeanContext(){}

  private static class InstanceHolder {
    private static final SCGRouterConfigBeanContext INSTANCE = new SCGRouterConfigBeanContext();
  }

  public static SCGRouterConfigBeanContext context() {
    return InstanceHolder.INSTANCE;
  }

  // ~~

  private static final Logger log = LoggerFactory.getLogger(SCGRouterConfigBeanContext.class);

  private static final AtomicReference<SCGRouterConfigBean> reference = new AtomicReference<>();

  /**
   * Refresh Router Config Value .
   * @param bean instance of {@link SCGRouterConfigBean}
   */
  public void refresh(SCGRouterConfigBean bean) {
    log.debug("refresh router config bean , newest : \r\n {}", to(bean));
    SCGRouterConfigBean origin = reference.getAndSet(bean);
    log.debug("[Origin] Config Value is : \r\n {}", to(origin));
  }

  public SCGRouterConfigBean getNewestConfigBean() {
    return reference.get();
  }

}
