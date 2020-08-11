package com.yunlsp.framework.ingress.integrate.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.yunlsp.framework.ingress.core.access.AccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import xyz.vopen.mixmicro.kits.StringUtils;
import xyz.vopen.mixmicro.kits.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * {@link ZuulRouterRreAccessFilter}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/4/3
 */
public class ZuulRouterRreAccessFilter extends ZuulFilter {

  private static final Logger log = LoggerFactory.getLogger(ZuulRouterRreAccessFilter.class);

  private final ZuulPluginProperties properties;

  private final AccessService accessService;

  public ZuulRouterRreAccessFilter(ZuulPluginProperties properties, AccessService accessService) {
    this.properties = properties;
    this.accessService = accessService;
  }

  private final static Map<String, Pattern> PATTERNS = new HashMap<>();

  @Override
  public String filterType() {
    return FilterConstants.PRE_TYPE;
  }

  @Override
  public int filterOrder() {
    return -1;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() throws ZuulException {

    RequestContext ctx = RequestContext.getCurrentContext();

    String uri = ctx.getRequest().getRequestURI();

    if (StringUtils.isNotBlank(uri)) {
      if (doCheck(uri)) {
        processResponse(ctx, false, 403, "Forbidden Explained");
      }
    }

    return null;
  }

  private boolean doCheck(@NonNull String uri) {
    return this.accessService.checkAccessAllow(uri);
  }


  /**
   * Process Response
   *
   * @param ctx              ctx
   * @param sendZuulResponse response
   * @param statusCode       status code
   * @param responseBody     body
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
