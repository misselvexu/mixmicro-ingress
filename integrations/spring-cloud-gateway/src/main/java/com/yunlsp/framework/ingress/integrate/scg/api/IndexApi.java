package com.yunlsp.framework.ingress.integrate.scg.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * {@link IndexApi}
 *
 * <p>Class IndexApi Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/24
 */
@RestController
public class IndexApi {

  @RequestMapping("/")
  public Mono<String> index() {
    return Mono.just("ingress cloud gateway");
  }
}
