package com.yunlsp.framework.ingress.integrate.scg.listener;

import com.yunlsp.framework.ingress.integrate.scg.service.SCGRouterExtConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

/**
 * {@link SCGApplicationLifecycleListener}
 *
 * <p>Class SCGApplicationLifecycleListener Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/23
 */
public class SCGApplicationLifecycleListener implements ApplicationListener<ApplicationEvent> {

  private static final Logger log = LoggerFactory.getLogger(SCGApplicationLifecycleListener.class);

  /**
   * Handle an application event.
   *
   * @param event the event to respond to
   */
  @Override
  public void onApplicationEvent(@NonNull ApplicationEvent event) {

    if(event instanceof ApplicationStartedEvent) {

      ApplicationStartedEvent startedEvent = (ApplicationStartedEvent) event;

      ConfigurableApplicationContext context = startedEvent.getApplicationContext();

      SCGRouterExtConfigService service = context.getBean(SCGRouterExtConfigService.class);

      service.initialize();

      log.info("[==SCG==] scg router ext config service is initialized .");

    }


  }
}
