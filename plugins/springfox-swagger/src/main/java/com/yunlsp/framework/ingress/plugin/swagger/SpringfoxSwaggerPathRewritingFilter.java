package com.yunlsp.framework.ingress.plugin.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.netflix.zuul.context.RequestContext;
import com.yunlsp.framework.ingress.IngressProperties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import springfox.documentation.swagger2.web.Swagger2Controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.zip.GZIPInputStream;

/**
 * {@link SpringfoxSwaggerPathRewritingFilter}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/24
 */
public class SpringfoxSwaggerPathRewritingFilter extends SendResponseFilter {

  private final Logger log = LoggerFactory.getLogger(SpringfoxSwaggerPathRewritingFilter.class);

  private ObjectMapper mapper = new ObjectMapper();

  private final IngressProperties ingressProperties;

  public SpringfoxSwaggerPathRewritingFilter(
      IngressProperties ingressProperties, ZuulProperties zuulProperties) {
    super(zuulProperties);
    this.ingressProperties = ingressProperties;
  }

  @Override
  public String filterType() {
    return FilterConstants.POST_TYPE;
  }

  @Override
  public int filterOrder() {
    return 100;
  }

  @Override
  public boolean shouldFilter() {
    return RequestContext.getCurrentContext()
        .getRequest()
        .getRequestURI()
        .endsWith(Swagger2Controller.DEFAULT_URL);
  }

  @Override
  public Object run() {
    RequestContext context = RequestContext.getCurrentContext();

    if (!context.getResponseGZipped()) {
      context.getResponse().setCharacterEncoding("UTF-8");
    }

    String rewrittenResponse = rewriteBasePath(context);
    context.setResponseBody(rewrittenResponse);
    return null;
  }

  @SuppressWarnings("unchecked")
  private String rewriteBasePath(RequestContext context) {
    InputStream responseDataStream = context.getResponseDataStream();
    String requestUri = RequestContext.getCurrentContext().getRequest().getRequestURI();
    try {
      if (context.getResponseGZipped()) {
        responseDataStream = new GZIPInputStream(context.getResponseDataStream());
      }
      String response = IOUtils.toString(responseDataStream, StandardCharsets.UTF_8);

      LinkedHashMap<String, Object> map = this.mapper.readValue(response, LinkedHashMap.class);

      String basePath = requestUri.replace(Swagger2Controller.DEFAULT_URL, "").replace("//", "/");

      // fix basePath -> /
      map.put("basePath", "/");

      // fix local request & fix 443 -> 80
      if (map.containsKey("host") && ingressProperties.isSsl()) {
        String[] host = ((String) map.get("host")).split(":");
        map.put("host", host[0] + ":" + 443);
      }

      if (map.containsKey("paths")) {
        Object pathsObject = map.get("paths");
        if (pathsObject instanceof LinkedHashMap) {
          LinkedHashMap<String, Object> temp = (LinkedHashMap<String, Object>) pathsObject;
          LinkedHashMap<String, Object> targetPathMap = Maps.newLinkedHashMap();
          temp.forEach(
              (path, value) -> targetPathMap.put(basePath.replace("//", "/") + path, value));
          map.put("paths", targetPathMap);
        }
      }

      log.debug("swagger-docs: rewritten Base URL with correct micro-service route: {}", basePath);
      return mapper.writeValueAsString(map);

    } catch (IOException e) {
      log.error("swagger-docs filter error", e);
    }
    return null;
  }
}
