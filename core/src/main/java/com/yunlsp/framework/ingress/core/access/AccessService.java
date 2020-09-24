package com.yunlsp.framework.ingress.core.access;

import com.yunlsp.framework.ingress.core.DefaultIngressProperties;

/**
 * {@link AccessService}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 8/10/20
 */
public class AccessService {

  private final DefaultIngressProperties defaultIngressProperties;

  public AccessService(DefaultIngressProperties defaultIngressProperties) {
    this.defaultIngressProperties = defaultIngressProperties;
  }

  public boolean checkAccessAllow(String uri) {

    // check insensitive
    if (this.defaultIngressProperties.getInsensitiveUrls().stream().anyMatch(uri::contains)
        || this.defaultIngressProperties.getInsensitiveUrls().stream().anyMatch(uri::matches)) {
      return false;
    }

    return this.defaultIngressProperties.getAccessUrls().stream().anyMatch(uri::contains)
        || this.defaultIngressProperties.getAccessUrls().stream().anyMatch(uri::matches);
  }
}
