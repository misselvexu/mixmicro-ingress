package com.yunlsp.framework.ingress.plugin.sentinel.gateway.zuul;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.callback.ZuulGatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulErrorFilter;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPostFilter;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPreFilter;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.netflix.zuul.http.ZuulServlet;
import com.yunlsp.framework.ingress.plugin.sentinel.gateway.ConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * Sentinel Spring Cloud Zuul AutoConfiguration.
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ZuulServlet.class)
@ConditionalOnProperty(
    prefix = ConfigConstants.ZUUL_PREFIX,
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties(SentinelZuulProperties.class)
public class SentinelZuulAutoConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(SentinelZuulAutoConfiguration.class);

  private final Optional<RequestOriginParser> requestOriginParserOptional;

  private final SentinelZuulProperties zuulProperties;

  public SentinelZuulAutoConfiguration(
      Optional<RequestOriginParser> requestOriginParserOptional,
      SentinelZuulProperties zuulProperties) {
    this.requestOriginParserOptional = requestOriginParserOptional;
    this.zuulProperties = zuulProperties;
  }

  @PostConstruct
  private void init() {
    requestOriginParserOptional.ifPresent(ZuulGatewayCallbackManager::setOriginParser);
    System.setProperty(
        SentinelConfig.APP_TYPE, ConfigConstants.APP_TYPE_ZUUL_GATEWAY);
  }

  @Bean
  @ConditionalOnMissingBean
  public SentinelZuulPreFilter sentinelZuulPreFilter() {
    logger.info(
        "[Sentinel Zuul] register SentinelZuulPreFilter {}", zuulProperties.getOrder().getPre());
    return new SentinelZuulPreFilter(zuulProperties.getOrder().getPre());
  }

  @Bean
  @ConditionalOnMissingBean
  public SentinelZuulPostFilter sentinelZuulPostFilter() {
    logger.info(
        "[Sentinel Zuul] register SentinelZuulPostFilter {}", zuulProperties.getOrder().getPost());
    return new SentinelZuulPostFilter(zuulProperties.getOrder().getPost());
  }

  @Bean
  @ConditionalOnMissingBean
  public SentinelZuulErrorFilter sentinelZuulErrorFilter() {
    logger.info(
        "[Sentinel Zuul] register SentinelZuulErrorFilter {}",
        zuulProperties.getOrder().getError());
    return new SentinelZuulErrorFilter(zuulProperties.getOrder().getError());
  }

  @Bean
  public FallBackProviderHandler fallBackProviderHandler(DefaultListableBeanFactory beanFactory) {
    return new FallBackProviderHandler(beanFactory);
  }
}
