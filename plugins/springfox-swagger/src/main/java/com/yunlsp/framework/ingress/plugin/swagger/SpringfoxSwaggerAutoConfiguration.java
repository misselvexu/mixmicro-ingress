package com.yunlsp.framework.ingress.plugin.swagger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Optional;

import static com.yunlsp.framework.ingress.IngressProperties.INGRESS_PROPERTIES_PREFIX;

/**
 * {@link SpringfoxSwaggerAutoConfiguration}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = INGRESS_PROPERTIES_PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties(SpringfoxSwaggerProperties.class)
public class SpringfoxSwaggerAutoConfiguration {

  @Bean
  public Docket createRestApi(SpringfoxSwaggerProperties properties) {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(
            new ApiInfoBuilder()
                .title(
                    Optional.ofNullable(properties.getTitle())
                        .orElse("Mixmicro Ingress Cohesive Document Reference"))
                .description(
                    Optional.ofNullable(properties.getDescription())
                        .orElse("Mixmicro Ingress All Exported Api Documents"))
                .version(properties.getVersion())
                .contact(properties.getContact())
                .build());
  }
}
