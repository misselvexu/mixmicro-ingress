package com.yunlsp.framework.ingress.plugin.swagger.autoconfigure;

import com.yunlsp.framework.ingress.plugin.swagger.SCGDocumentProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.yunlsp.framework.ingress.plugin.swagger.SCGDocumentProperties.SCG_DOCUMENT_PROPERTIES_PREFIX;

/**
 * {@link SCGDocumentHandler}
 *
 * <p>Class SCGDocumentHandler Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/24
 */
@ConditionalOnProperty(
    prefix = SCG_DOCUMENT_PROPERTIES_PREFIX,
    value = "enabled",
    havingValue = "true")
@RestController
public class SCGDocumentHandler {

  @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
  @Autowired(required = false)
  private UiConfiguration uiConfiguration;
  private final SwaggerResourcesProvider swaggerResources;
  private final SCGDocumentProperties properties;

  public SCGDocumentHandler(SwaggerResourcesProvider swaggerResources, SCGDocumentProperties properties) {
    this.swaggerResources = swaggerResources;
    this.properties = properties;
  }


  @GetMapping("/swagger-resources/configuration/security")
  public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
    return Mono.just(
        new ResponseEntity<>(SecurityConfigurationBuilder.builder().build(), HttpStatus.OK));
  }

  @GetMapping("/swagger-resources/configuration/ui")
  public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
    return Mono.just(
        new ResponseEntity<>(
            Optional.ofNullable(uiConfiguration).orElse(UiConfigurationBuilder.builder().build()),
            HttpStatus.OK));
  }

  @GetMapping("/swagger-resources")
  public Mono<ResponseEntity<List<SwaggerResource>>> swaggerResources() {
    List<SwaggerResource> swaggerResources = this.swaggerResources.get();
    List<SwaggerResource> filterList = new ArrayList<>();
    String resources = properties.getResources();
    String[] resourcesArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(resources, ",");
    if (resourcesArray != null && resources.length() > 0) {
      boolean include = false;
      for (SwaggerResource resource : swaggerResources) {
        if (Arrays.stream(resourcesArray)
            .anyMatch(r -> StringUtils.equalsIgnoreCase(r, resource.getName()))) {
          include = true;
        }
        if (include) {
          filterList.add(resource);
        }
      }
      return Mono.just((new ResponseEntity<>(filterList, HttpStatus.OK)));
    }
    return Mono.just((new ResponseEntity<>(swaggerResources, HttpStatus.OK)));
  }
}
