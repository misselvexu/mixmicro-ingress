package com.yunlsp.framework.ingress.plugin.router;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.*;
import org.springframework.lang.NonNull;

/**
 * {@link ZuulDynamicRouterListener}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
public class ZuulDynamicRouterListener
    implements ApplicationListener<RefreshEvent>, ApplicationContextAware {

  private ApplicationContext context;

  private final RouteLocator routeLocator;

  /**
   * Constructor with parameter instance of {@link RouteLocator}
   *
   * @param routeLocator A map of route path (pattern) to location (e.g. service id or URL).
   */
  public ZuulDynamicRouterListener(RouteLocator routeLocator) {
    this.routeLocator = routeLocator;
  }

  /**
   * Handle an application event.
   *
   * @param event the event to respond to
   */
  @Override
  public void onApplicationEvent(@NonNull RefreshEvent event) {
    this.context.publishEvent(new RoutesRefreshedEvent(routeLocator));
  }

  /**
   * Set the ApplicationContext that this object runs in. Normally this call will be used to
   * initialize the object.
   *
   * <p>Invoked after population of normal bean properties but before an init callback such as
   * {@link InitializingBean#afterPropertiesSet()} or a custom init-method. Invoked after {@link
   * ResourceLoaderAware#setResourceLoader}, {@link
   * ApplicationEventPublisherAware#setApplicationEventPublisher} and {@link MessageSourceAware}, if
   * applicable.
   *
   * @param context the ApplicationContext object to be used by this object
   * @throws ApplicationContextException in case of context initialization errors
   * @throws BeansException if thrown by application context methods
   * @see BeanInitializationException
   */
  @Override
  public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
    this.context = context;
  }
}
