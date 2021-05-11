package com.yunlsp.framework.ingress.plugin.router;

import com.google.common.annotations.Beta;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static com.yunlsp.framework.ingress.IngressProperties.INGRESS_PROPERTIES_PREFIX;
import static com.yunlsp.framework.ingress.plugin.router.SCGDefaultRetryProperties.SCG_RETRY_PROPERTIES_PREFIX;

/**
 * {@link SCGDefaultRetryProperties}
 *
 * <p>Class SCGDefaultRetryProperties Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2021/1/7
 */
@Getter
@Setter
@ConfigurationProperties(prefix = SCG_RETRY_PROPERTIES_PREFIX)
public class SCGDefaultRetryProperties implements Serializable {

  public static final String SCG_RETRY_PROPERTIES_PREFIX = INGRESS_PROPERTIES_PREFIX + ".scg.dynamic.retry";

  public static final String RETRY_FILTER_NAME = "Retry";

  @Builder.Default private boolean enabled = false;

  private Map<String, String> args = new LinkedHashMap<>();


  @Getter
  @Setter
  @Builder
  @Beta
  @NoArgsConstructor
  @AllArgsConstructor
  static class DefaultRetryFilterDefinition {

    private int retries = 2;

    private List<HttpStatus.Series> series = toList(HttpStatus.Series.SERVER_ERROR);

    private List<HttpStatus> statuses = new ArrayList<>();

    private List<HttpMethod> methods = toList(HttpMethod.GET);

    private List<Class<? extends Throwable>> exceptions = toList(IOException.class, TimeoutException.class);

  }


  @SafeVarargs
  private static <T> List<T> toList(T... items) {
    return new ArrayList<>(Arrays.asList(items));
  }
}
