package com.yunlsp.framework.ingress.plugin.router.context;

import com.google.gson.GsonBuilder;
import com.yunlsp.framework.ingress.plugin.router.core.model.SCGRouteDefinition;
import org.junit.Test;

import java.util.HashMap;
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
    System.out.println(
        new GsonBuilder().setPrettyPrinting().create().toJson(SCGRouteDefinition.builder().metadata(metadata))
    );

  }


}