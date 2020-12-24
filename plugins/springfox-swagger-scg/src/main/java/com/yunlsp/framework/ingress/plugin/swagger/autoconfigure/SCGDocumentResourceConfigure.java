package com.yunlsp.framework.ingress.plugin.swagger.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link SCGDocumentResourceConfigure}
 *
 * <p>Class SCGDocumentResourceConfigure Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/24
 */
@Primary
public class SCGDocumentResourceConfigure implements SwaggerResourcesProvider {

  private static final Logger log = LoggerFactory.getLogger(SCGDocumentResourceConfigure.class);

  private final RouteDefinitionLocator routeDefinitionLocator;

  public SCGDocumentResourceConfigure(RouteDefinitionLocator routeDefinitionLocator) {
    this.routeDefinitionLocator = routeDefinitionLocator;
  }

  @Override
  public List<SwaggerResource> get() {

    List<SwaggerResource> resources = new ArrayList<>();

    routeDefinitionLocator
        .getRouteDefinitions()
        .subscribe(
            routeDefinition -> {
              routeDefinition.getPredicates().stream()
                  .filter(
                      predicateDefinition ->
                          ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                  .forEach(
                      predicateDefinition -> {
                        if (predicateDefinition.getArgs().get("pattern") != null) {
                          resources.add(
                              swaggerResource(
                                  routeDefinition.getId(),
                                  predicateDefinition
                                      .getArgs()
                                      .get("pattern")
                                      .replace("**", "v2/api-docs")));
                        } else {

                          resources.add(
                              swaggerResource(
                                  routeDefinition.getId(),
                                  predicateDefinition
                                      .getArgs()
                                      .get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                      .replace("**", "v2/api-docs")));
                        }
                      });
            });

    if(log.isInfoEnabled()) {
      log.info("[==SCG==] dynamic router resource size: {}", resources.size());
    }

    return resources;
  }

  private SwaggerResource swaggerResource(String name, String location) {
    SwaggerResource swaggerResource = new SwaggerResource();
    swaggerResource.setName(name);
    swaggerResource.setLocation(location);
    swaggerResource.setSwaggerVersion("2.0");
    return swaggerResource;
  }
}
