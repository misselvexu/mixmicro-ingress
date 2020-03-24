package com.yunlsp.framework.ingress.integrate.zuul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.*;
import org.springframework.lang.NonNull;

/**
 * {@link ZuulPropertiesBeanPostProcessor}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/24
 */
public class ZuulPropertiesBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

  private static final Logger log = LoggerFactory.getLogger(ZuulPropertiesBeanPostProcessor.class);

  private ApplicationContext context;

  @Override
  public Object postProcessBeforeInitialization(@NonNull Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(@NonNull Object bean, String beanName)
      throws BeansException {

    if (bean instanceof ZuulPluginProperties.ZuulRouterExtendedProperties) {
      log.debug("[ZEP] {}", bean.getClass());
    }

    if (bean instanceof ZuulProperties) {
      ZuulProperties properties = (ZuulProperties) bean;
      ZuulPluginProperties.ZuulRouterExtendedProperties zep = this.context.getBean(ZuulPluginProperties.ZuulRouterExtendedProperties.class);
      BeanUtils.copyProperties(zep,properties);
      log.info("[ZEP] extended zuul route properties is processed.");
    }

    return bean;
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
