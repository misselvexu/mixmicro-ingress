package com.yunlsp.framework.ingress.plugin.router.context;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.yunlsp.framework.ingress.plugin.router.core.model.SCGFilterDefinition;
import com.yunlsp.framework.ingress.plugin.router.core.model.SCGRouteDefinition;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link SCGDynamicRouterServiceTest}
 *
 * <p>Class SCGDynamicRouterServiceTest Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2021/1/11
 */
public class SCGDynamicRouterServiceTest {

  @Test
  public void metadata0() throws Exception {

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("connect-timeout", 2000);
    metadata.put("response-timeout", 2000);

    List<SCGFilterDefinition> filters = Lists.newArrayList();
    // Retry Filter
    Map<String, String> rm = Maps.newHashMap();
    rm.put("retries", "2");
    rm.put("series", "SERVER_ERROR");
    rm.put("statuses", "INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT");
    rm.put("methods", "GET");
    rm.put("exceptions", "java.io.IOException, org.springframework.cloud.gateway.support.TimeoutException");

    filters.add(SCGFilterDefinition.builder()
        .name("Retry")
        .args(rm)
        .build());

    System.out.println(
        new GsonBuilder().setPrettyPrinting().create().toJson(SCGRouteDefinition.builder()
            .filters(filters)
            .metadata(metadata))
    );

  }


}