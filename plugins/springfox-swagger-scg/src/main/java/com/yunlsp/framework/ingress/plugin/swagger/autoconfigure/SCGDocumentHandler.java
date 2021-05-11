package com.yunlsp.framework.ingress.plugin.swagger.autoconfigure;

import com.yunlsp.framework.ingress.plugin.swagger.SCGDocumentProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

  private final SwaggerResourcesProvider swaggerResourcesProvider;
  private final SCGDocumentProperties properties;

  public SCGDocumentHandler(
      SwaggerResourcesProvider swaggerResourcesProvider, SCGDocumentProperties properties) {
    this.swaggerResourcesProvider = swaggerResourcesProvider;
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
    List<SwaggerResource> swaggerResources = this.swaggerResourcesProvider.get();
    Set<String> resources = properties.getResources();
    // 获取暴露doc的服务列表 过滤资源 resources实际上不会有为空的情况
    swaggerResources =
        swaggerResources.stream()
            .filter(swaggerResource -> resources.contains(swaggerResource.getName()))
            .sorted()
            .collect(Collectors.toList());
    return Mono.just((new ResponseEntity<>(swaggerResources, HttpStatus.OK)));
  }
}
