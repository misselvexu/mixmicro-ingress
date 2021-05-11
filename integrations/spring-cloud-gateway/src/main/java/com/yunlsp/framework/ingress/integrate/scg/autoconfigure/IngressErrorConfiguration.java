package com.yunlsp.framework.ingress.integrate.scg.autoconfigure;

import com.yunlsp.framework.ingress.integrate.scg.handler.IngressExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.List;

/**
 * {@link IngressErrorConfiguration}
 *
 * <p>Class IngressErrorConfiguration Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/24
 */
@RequiredArgsConstructor
public class IngressErrorConfiguration {

  private final ServerProperties serverProperties;
  private final ApplicationContext applicationContext;
  private final ResourceProperties resourceProperties;
  private final List<ViewResolver> viewResolvers;
  private final ServerCodecConfigurer serverCodecConfigurer;

  @Bean
  @Primary
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
    IngressExceptionHandler exceptionHandler =
        new IngressExceptionHandler(
            errorAttributes,
            this.resourceProperties,
            this.serverProperties.getError(),
            this.applicationContext);
    exceptionHandler.setViewResolvers(this.viewResolvers);
    exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
    exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
    return exceptionHandler;
  }
}
