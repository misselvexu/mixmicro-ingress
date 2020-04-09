package com.yunlsp.framework.ingress.integrate.zuul;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import xyz.vopen.mixmicro.components.common.ResponseEntity;
import xyz.vopen.mixmicro.components.common.SerializableBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

/**
 * {@link DefaultFallbackProvider}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/24
 */
public class DefaultFallbackProvider implements FallbackProvider {

  private static final Logger log = LoggerFactory.getLogger(DefaultFallbackProvider.class);

  /**
   * The route this fallback will be used for.
   *
   * @return The route the fallback will be used for.
   */
  @Override
  public String getRoute() {
    return null;
  }

  /**
   * Provides a fallback response based on the cause of the failed execution.
   *
   * @param route The route the fallback is for
   * @param cause cause of the main method failure, may be <code>null</code>
   * @return the fallback response
   */
  @Override
  public ClientHttpResponse fallbackResponse(String route, Throwable cause) {

    if (log.isDebugEnabled()) {
      log.error("[INGRESS Fallback Provider] route exception", cause);
    }

    if (cause instanceof SocketTimeoutException) {

      return response(
          HttpStatus.GATEWAY_TIMEOUT,
          route,
          ResponseEntity.fail(
              Void.class, INTERNAL_SERVER_ERROR.value(), "Service " + route + " process timeout"));

    } else {

      return response(
          INTERNAL_SERVER_ERROR,
          route,
          ResponseEntity.fail(
              Void.class, INTERNAL_SERVER_ERROR.value(), "Service " + route + " process failed"));
    }
  }

  private ClientHttpResponse response(
      final HttpStatus status, String route, final ResponseEntity<?> entity) {

    return new ClientHttpResponse() {

      @Override
      @NonNull
      public HttpStatus getStatusCode() {
        return status;
      }

      @Override
      public int getRawStatusCode() {
        return status.value();
      }

      @Override
      @NonNull
      public String getStatusText() {
        return status.getReasonPhrase();
      }

      @Override
      public void close() {}

      @Override
      @NonNull
      public InputStream getBody() {
        return new ByteArrayInputStream(SerializableBean.bytes(entity));
      }

      @Override
      @NonNull
      public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
      }
    };
  }
}
