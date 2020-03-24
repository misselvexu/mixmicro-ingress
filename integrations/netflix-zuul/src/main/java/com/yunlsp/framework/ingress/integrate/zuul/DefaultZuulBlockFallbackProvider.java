package com.yunlsp.framework.ingress.integrate.zuul;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * {@link DefaultZuulBlockFallbackProvider}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/24
 */
public class DefaultZuulBlockFallbackProvider implements ZuulBlockFallbackProvider {
  /**
   * The route this fallback will be used for.
   *
   * @return The route the fallback will be used for.
   */
  @Override
  public String getRoute() {
    return "*";
  }

  /**
   * Provides a fallback response based on the cause of the failed execution.
   *
   * @param route The route the fallback is for
   * @param cause cause of the main method failure, may be <code>null</code>
   * @return the fallback response
   */
  @Override
  public BlockResponse fallbackResponse(String route, Throwable cause) {

    RecordLog.info(String.format("[Sentinel DefaultBlockFallbackProvider] Run fallback route: %s", route));

    if (cause instanceof BlockException) {
      return new BlockResponse(429, "Sentinel block exception", route);
    } else {
      return new BlockResponse(503, "Service " + route + " is unavailable", route);
    }
  }
}
