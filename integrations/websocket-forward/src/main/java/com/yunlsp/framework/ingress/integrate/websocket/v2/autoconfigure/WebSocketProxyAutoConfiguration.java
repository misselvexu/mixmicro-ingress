package com.yunlsp.framework.ingress.integrate.websocket.v2.autoconfigure;

import com.yunlsp.framework.ingress.integrate.websocket.v2.WebSocketProxyManager;
import com.yunlsp.framework.ingress.integrate.websocket.v2.WebSocketProxyProperties;
import com.yunlsp.framework.ingress.integrate.websocket.v2.WebSocketProxyServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.vopen.mixmicro.kits.lang.NonNull;

import static com.yunlsp.framework.ingress.integrate.websocket.v2.WebSocketProxyProperties.WEBSOCKET_PROPERTIES_PREFIX;
import static com.yunlsp.framework.ingress.integrate.websocket.v2.context.WebSocketProxyContext.WEBSOCKET_BEAN_NAME;
import static com.yunlsp.framework.ingress.integrate.websocket.v2.context.WebSocketProxyContext.context;

/**
 * {@link WebSocketProxyAutoConfiguration}
 *
 * <p>Class WebSocketProxyAutoConfiguration Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/15
 */
@Configuration
@EnableConfigurationProperties(WebSocketProxyProperties.class)
@ConditionalOnProperty(
    prefix = WEBSOCKET_PROPERTIES_PREFIX,
    value = "enabled",
    havingValue = "true")
public class WebSocketProxyAutoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(WebSocketProxyAutoConfiguration.class);

  // ~~ bean definition ~~

  @Bean
  public ApplicationStartedEventListener applicationStartedEventListener() {
    return new ApplicationStartedEventListener();
  }


  // ~~ application started event listener .

  public static class ApplicationStartedEventListener implements ApplicationListener<ApplicationEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {

      if(event instanceof ApplicationStartedEvent) {
        ConfigurableApplicationContext context = ((ApplicationStartedEvent) event).getApplicationContext();
        if(!context.containsBean(WEBSOCKET_BEAN_NAME)) {
          context.getBeanFactory().registerSingleton(WEBSOCKET_BEAN_NAME, context());
          log.info("[WSSP] registered 'WebSocketProxyContext' into spring context factory ~");
        }

        WebSocketProxyProperties proxyProperties = context.getBean(WebSocketProxyProperties.class);
        context().registerWebSocketProxyProperties(proxyProperties);
        log.info("[WSSP] registered 'WebSocketProxyProperties' into proxy context bean ~");

        // ~~ startup websocket proxy server
        WebSocketProxyManager.getInstance().init();

        log.info("[WSSP] startup websocket proxy server ...");
        DeploymentOptions options = new DeploymentOptions();
        options.setInstances(Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
        Vertx.vertx().deployVerticle(WebSocketProxyServer.class.getName(), options);
        log.info("[WSSP] websocket proxy server started , port: {}", proxyProperties.getPort());
      }
    }
  }

}
