package com.yunlsp.framework.ingress;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * {@link IngressServer}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class IngressServer {

  public static void main(String[] args) {

    new SpringApplicationBuilder()
        .sources(IngressServer.class)
        // default properties
        .properties("--spring.profiles.active=production")
        .web(WebApplicationType.SERVLET)
        .run(args);
  }
}
