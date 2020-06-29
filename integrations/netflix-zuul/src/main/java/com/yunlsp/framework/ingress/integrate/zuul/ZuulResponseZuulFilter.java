package com.yunlsp.framework.ingress.integrate.zuul;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import xyz.vopen.mixmicro.kits.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static xyz.vopen.mixmicro.components.common.MixmicroConstants.MIXMICRO_FEIGN_REQUEST_OVER_INGRESS;
import static xyz.vopen.mixmicro.components.common.MixmicroConstants.MIXMICRO_SERVICE_INVOKE_HEADER;

/**
 * {@link ZuulResponseZuulFilter}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/4/3
 */
public class ZuulResponseZuulFilter extends ZuulFilter {

  private static final Logger log = LoggerFactory.getLogger(ZuulResponseZuulFilter.class);

  @Override
  public String filterType() {
    return FilterConstants.POST_TYPE;
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

    int httpCode = ctx.getResponseStatusCode();

    try{
      HttpServletResponse response = ctx.getResponse();
      ClientHttpResponse proxyResponse = (ClientHttpResponse) ctx.get("zuulResponse");
      HttpHeaders headers = proxyResponse.getHeaders();
      if(headers.containsKey(SET_COOKIE)){
        List<String> cookies = headers.get(SET_COOKIE);
        if (cookies != null && !cookies.isEmpty()) {
          for (String cookie : cookies) {
            response.addHeader(SET_COOKIE, cookie);
          }
        }
      }
    } catch (Exception ignored) {
    }

    processResponse(ctx, true, httpCode, null);
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

    try{
      if(doCompatibleResponse(ctx, sendZuulResponse, statusCode, responseBody)) {
        return;
      }
    } catch (Exception ignore) {}

    ctx.setSendZuulResponse(sendZuulResponse);
    ctx.setResponseStatusCode(statusCode);
    if (StringUtils.isNotBlank(responseBody) && !sendZuulResponse) {
      ctx.setResponseBody(responseBody);
    }
  }

  private static final String RESPONSE_CODE_KEY = "code";

  private boolean doCompatibleResponse(RequestContext ctx, boolean sendZuulResponse, Integer statusCode, String responseBody) {

    try{
      String requestOverIngress = ctx.getRequest().getHeader(MIXMICRO_FEIGN_REQUEST_OVER_INGRESS);

      if(StringUtils.isNotBlank(requestOverIngress) && "true".equalsIgnoreCase(requestOverIngress)) {

        String serviceInvokHeader = ctx.getRequest().getHeader(MIXMICRO_SERVICE_INVOKE_HEADER);

        if(StringUtils.isNotBlank(serviceInvokHeader)) {

          ctx.setSendZuulResponse(sendZuulResponse);

          JSONObject object = JSON.parseObject(responseBody);

          if(object.containsKey(RESPONSE_CODE_KEY) && object.getInteger(RESPONSE_CODE_KEY) == INTERNAL_SERVER_ERROR.value()) {
            ctx.setResponseStatusCode(INTERNAL_SERVER_ERROR.value());
            if (StringUtils.isNotBlank(responseBody) && !sendZuulResponse) {
              ctx.setResponseBody(responseBody);
              return true;
            }
          }
        }
      }

    } catch (Exception e) {
      log.warn("[==ZUUL==] decode request feign header exception", e);
    }

    return false;
  }


}
