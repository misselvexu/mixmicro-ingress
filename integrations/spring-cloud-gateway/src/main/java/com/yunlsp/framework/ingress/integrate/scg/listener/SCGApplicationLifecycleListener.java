package com.yunlsp.framework.ingress.integrate.scg.listener;

import com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigBean;
import com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigProperties;
import com.yunlsp.framework.ingress.integrate.scg.service.SCGRouterExtConfigService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.yunlsp.framework.ingress.common.IngressSerializers.to;
import static com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigBeanContext.context;

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

  private static final AtomicBoolean executed = new AtomicBoolean();

  /**
   * Handle an application event.
   *
   * @param event the event to respond to
   */
  @Override
  public void onApplicationEvent(@NonNull ApplicationEvent event) {

    if (event instanceof ApplicationStartedEvent) {

      ApplicationStartedEvent startedEvent = (ApplicationStartedEvent) event;

      ConfigurableApplicationContext context = startedEvent.getApplicationContext();

      ConfigurableEnvironment environment = context.getEnvironment();

      if (executed.compareAndSet(false, true)) {
        this.initRouterExtConfig(context, environment);
      }
    }
  }

  // ~
  private void initRouterExtConfig(ConfigurableApplicationContext context, ConfigurableEnvironment environment) {

    SCGRouterConfigProperties properties = context.getBean(SCGRouterConfigProperties.class);

    log.info("[==SCG==] load router ext config , type : {}", properties.getType());

    switch (properties.getType()) {
      case NACOS:
        SCGRouterExtConfigService service = context.getBean(SCGRouterExtConfigService.class);
        service.initialize();
        break;

      case FILE:
      default:
        try {
          SCGRouterConfigProperties.FileDataSource dataSource = properties.getFile();
          File file = ResourceUtils.getFile(StringUtils.trimAllWhitespace(dataSource.getResource()));
          String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
          SCGRouterConfigBean bean = SCGRouterConfigBean.load(content);
          context().refresh(bean);
        } catch (Exception e) {
          log.warn("[==SCG==] local ext config file loaded failed", e);
        }
        break;
    }

    log.info("[==SCG==] scg router ext config service is initialized , detail : \r\n {}", to(context().getNewestConfigBean()));
  }
}
