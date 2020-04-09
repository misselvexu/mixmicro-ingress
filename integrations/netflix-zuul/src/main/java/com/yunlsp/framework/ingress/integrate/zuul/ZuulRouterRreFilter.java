package com.yunlsp.framework.ingress.integrate.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import xyz.vopen.mixmicro.kits.StringUtils;

import static xyz.vopen.mixmicro.components.common.MixmicroConstants.MIXMICRO_INGRESS_INVOKE_HEADER;

/**
 * {@link ZuulRouterRreFilter}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/4/3
 */
public class ZuulRouterRreFilter extends ZuulFilter {

  private static final Logger log = LoggerFactory.getLogger(ZuulRouterRreFilter.class);

  private final ZuulPluginProperties properties;

  public ZuulRouterRreFilter(ZuulPluginProperties properties) {
    this.properties = properties;
  }

  @Override
  public String filterType() {
    return FilterConstants.PRE_TYPE;
  }

  @Override
  public int filterOrder() {
    return 0;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() throws ZuulException {

    RequestContext ctx = RequestContext.getCurrentContext();

    // ADD DEFAULT ZUUL HEADER
    ctx.addZuulRequestHeader(MIXMICRO_INGRESS_INVOKE_HEADER, "true");

    return null;
  }

  /**
   * Process Response
   *
   * @param ctx ctx
   * @param sendZuulResponse response
   * @param statusCode status code
   * @param responseBody body
   */
  @SuppressWarnings("SameParameterValue")
  private void processResponse(
      RequestContext ctx, boolean sendZuulResponse, Integer statusCode, String responseBody) {
    ctx.setSendZuulResponse(sendZuulResponse);
    ctx.setResponseStatusCode(statusCode);
    if (StringUtils.isNotBlank(responseBody) && !sendZuulResponse) {
      ctx.setResponseBody(responseBody);
    }
  }
}
