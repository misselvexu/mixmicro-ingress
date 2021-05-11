package com.yunlsp.framework.ingress.plugin.router.context.listener;

import com.yunlsp.framework.ingress.plugin.router.core.NacosDynamicConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link SCGLifecycleListener}
 *
 * <p>Class SCGLifecycleListener Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/19
 */
public class SCGLifecycleListener implements ApplicationListener<ApplicationEvent> {

  private static final Logger log = LoggerFactory.getLogger(SCGLifecycleListener.class);

  private static final AtomicBoolean configServiceStarted = new AtomicBoolean();

  private static final AtomicReference<NacosDynamicConfigService> nacosDynamicConfigServiceRef = new AtomicReference<>();

  /**
   * Handle an application event.
   *
   * @param event the event to respond to
   */
  @Override
  public void onApplicationEvent(@NonNull ApplicationEvent event) {

    // ~~ application started event .

    if(event instanceof ApplicationStartedEvent) {

      ApplicationStartedEvent startedEvent = (ApplicationStartedEvent) event;

      if(configServiceStarted.compareAndSet(false, true)) {

        ConfigurableApplicationContext context = startedEvent.getApplicationContext();

        NacosDynamicConfigService dynamicConfigService = context.getBean(NacosDynamicConfigService.class);
        nacosDynamicConfigServiceRef.set(dynamicConfigService);

        dynamicConfigService.initialize();
        log.info("[==SCG==] dynamic nacos config service is initialized .");
      }
    }


    // ~~ context shutdown event .

    if(event instanceof ContextStoppedEvent) {

      NacosDynamicConfigService dynamicConfigService = nacosDynamicConfigServiceRef.get();

      if(dynamicConfigService != null) {
        dynamicConfigService.shutdown();
      }

    }

  }
}
