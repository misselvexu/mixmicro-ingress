package com.yunlsp.framework.ingress;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

/**
 * {@link ZuulIngressServer}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class ZuulIngressServer {

  public static void main(String[] args) {

    new SpringApplicationBuilder()
        .sources(ZuulIngressServer.class)
        // default properties
        .properties("--spring.profiles.active=prod")
        .web(WebApplicationType.SERVLET)
        .run(args);
  }

  // 重试策略
  @Bean
  LoadBalancedRetryFactory retryFactory() {
    return new LoadBalancedRetryFactory() {
      @Override
      public BackOffPolicy createBackOffPolicy(String service) {
        return new ExponentialBackOffPolicy();
      }
    };
  }
}
