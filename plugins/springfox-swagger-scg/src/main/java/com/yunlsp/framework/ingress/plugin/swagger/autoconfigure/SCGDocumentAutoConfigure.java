package com.yunlsp.framework.ingress.plugin.swagger.autoconfigure;

import com.yunlsp.framework.ingress.plugin.router.autoconfigure.SCGDynamicRouterAutoConfiguration;
import com.yunlsp.framework.ingress.plugin.swagger.SCGDocumentProperties;
import com.yunlsp.framework.ingress.plugin.swagger.filter.SCGDocumentFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yunlsp.framework.ingress.plugin.swagger.SCGDocumentProperties.SCG_DOCUMENT_PROPERTIES_PREFIX;

/**
 * {@link SCGDocumentAutoConfigure}
 *
 * <p>Class SCGDocumentAutoConfigure Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/24
 */
@Configuration
@ConditionalOnProperty(
    prefix = SCG_DOCUMENT_PROPERTIES_PREFIX,
    value = "enabled",
    havingValue = "true")
@EnableConfigurationProperties(SCGDocumentProperties.class)
@AutoConfigureAfter(SCGDynamicRouterAutoConfiguration.class)
public class SCGDocumentAutoConfigure {

  @Bean
  public SCGDocumentResourceConfigure scgDocumentResourceConfigure(RouteDefinitionLocator routeDefinitionLocator) {
    return new SCGDocumentResourceConfigure(routeDefinitionLocator);
  }

  @Bean
  public SCGDocumentFilter scgDocumentFilter() {
    return new SCGDocumentFilter();
  }
}
