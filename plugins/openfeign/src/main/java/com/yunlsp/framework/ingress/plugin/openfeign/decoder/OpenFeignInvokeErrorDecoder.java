package com.yunlsp.framework.ingress.plugin.openfeign.decoder;

import feign.Feign;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import xyz.vopen.mixmicro.components.common.ResponseEntity;
import xyz.vopen.mixmicro.components.exception.defined.MixmicroInvokeException;

import static feign.FeignException.errorStatus;
import static xyz.vopen.mixmicro.components.exception.defined.MixmicroInvokeException.build;

/**
 * {@link OpenFeignInvokeErrorDecoder}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/5
 */
public class OpenFeignInvokeErrorDecoder implements ErrorDecoder {

  /**
   * Implement this method in order to decode an HTTP {@link Response} when {@link
   * Response#status()} is not in the 2xx range. Please raise application-specific exceptions where
   * possible. If your exception is retryable, wrap or subclass {@link RetryableException}
   *
   * @param methodKey {@link Feign#configKey} of the java method that invoked the request. ex.
   *     {@code IAM#getUser()}
   * @param response HTTP response where {@link Response#status() status} is greater than or equal
   *     to {@code 300}.
   * @return Exception IOException, if there was a network error reading the response or an
   *     application-specific exception decoded by the implementation. If the throwable is
   *     retryable, it should be wrapped, or a subtype of {@link RetryableException}
   */
  @Override
  public Exception decode(String methodKey, Response response) {

    if (response.status() >= 400 && response.status() <= 499) {
      return buildException(response, true);
    }

    if (response.status() >= 500 && response.status() <= 599) {
      return buildException(response, false);
    }

    // DEFAULT ERROR
    return errorStatus(methodKey, response);
  }

  @SuppressWarnings("unchecked")
  private MixmicroInvokeException buildException(Response response, boolean isClientSide) {
    try {
      byte[] content = IOUtils.toByteArray(response.body().asInputStream());
      ResponseEntity<String> entity = ResponseEntity.decode(content, ResponseEntity.class);
      return build(
          new MixmicroInvokeException(response.status(), entity.getMessage()), isClientSide);
    } catch (Exception e) {
      return new MixmicroInvokeException(response.status(), response.reason());
    }
  }
}
