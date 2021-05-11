package com.yunlsp.framework.ingress;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * {@link SCGIngressServer}
 *
 * <p>Class IngressServer Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/18
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class SCGIngressServer {

  public static void main(String[] args) {
    new SpringApplicationBuilder()
        .sources(SCGIngressServer.class)
        .properties("--spring.profiles.active=prod")
        .web(WebApplicationType.REACTIVE)
        .run(args);
  }
}
