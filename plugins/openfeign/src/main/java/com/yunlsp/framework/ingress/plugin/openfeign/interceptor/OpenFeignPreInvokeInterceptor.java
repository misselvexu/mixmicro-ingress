package com.yunlsp.framework.ingress.plugin.openfeign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import static xyz.vopen.mixmicro.components.common.MixmicroConstants.MIXMICRO_INGRESS_INVOKE_HEADER;

/**
 * {@link OpenFeignPreInvokeInterceptor}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/20
 */
public class OpenFeignPreInvokeInterceptor implements RequestInterceptor {

  /**
   * Called for every request. Add data using methods on the supplied {@link RequestTemplate}.
   *
   * @param template Request
   */
  @Override
  public void apply(RequestTemplate template) {
    template.header(MIXMICRO_INGRESS_INVOKE_HEADER, "mixmicro.ingress.zuul");
  }
}
