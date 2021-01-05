package com.yunlsp.framework.ingress.plugin.router.core;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunlsp.framework.ingress.plugin.router.SCGDynamicRouterProperties;
import com.yunlsp.framework.ingress.plugin.router.context.SCGDynamicRouterService;
import com.yunlsp.framework.ingress.plugin.router.core.model.SCGRouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link NacosDynamicConfigService}
 *
 * <p>Class NacosDynamicConfigService Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/19
 */
public class NacosDynamicConfigService {

  private static final Logger log = LoggerFactory.getLogger(NacosDynamicConfigService.class);

  private final SCGDynamicRouterService dynamicRouterService;

  private final SCGDynamicRouterProperties dynamicRouterProperties;

  private final ObjectMapper mapper = new ObjectMapper();

  public NacosDynamicConfigService(
      SCGDynamicRouterService dynamicRouterService,
      SCGDynamicRouterProperties dynamicRouterProperties) {
    this.dynamicRouterService = dynamicRouterService;
    this.dynamicRouterProperties = dynamicRouterProperties;
  }

  private ConfigService configService;

  private static final AtomicBoolean configServiceInitialized = new AtomicBoolean();

  public void initialize() {

    if (configServiceInitialized.compareAndSet(false, true)) {
      try {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESPACE, dynamicRouterProperties.getNamespace());
        properties.put(PropertyKeyConst.SERVER_ADDR, dynamicRouterProperties.getServerAddr());

        configService = NacosFactory.createConfigService(properties);

        log.info("[==SCG==] dynamic-router s nacos config service is created , instance: {}", configService);

        String configInfo =
            configService.getConfigAndSignListener(
                dynamicRouterProperties.getDataId(),
                dynamicRouterProperties.getDataGroup(),
                dynamicRouterProperties.getDefaultTimeout(),
                new AbstractListener() {
                  @Override
                  public void receiveConfigInfo(String configInfo) {

                    try{
                      log.info("[==SCG==] <<< received dynamic router content : \r\n {}", configInfo);
                      List<SCGRouteDefinition> definitions = mapper.readValue(configInfo, new TypeReference<List<SCGRouteDefinition>>(){});

//                      definitions.forEach(NacosDynamicConfigService.this::refresh);

                      refresh(definitions);

                    } catch (Exception e) {
                      log.warn("[==SCG==] process nacos server pushed config stream failed", e);
                    }
                  }
                });

        log.info("[==SCG==] >>> load dynamic router content : \r\n {}", configInfo);

        // refresh after first initialized .
        List<SCGRouteDefinition> definitions = mapper.readValue(configInfo, new TypeReference<List<SCGRouteDefinition>>(){});

//        definitions.forEach(this::refresh);
        refresh(definitions);

        log.info("[==SCG==] router is refreshed ~");

      } catch (Exception e) {
        throw new RuntimeException("nacos config service execute happened exception .", e);
      }
    }
  }

  public void shutdown() {
    if(configServiceInitialized.compareAndSet(true, false)) {
      if(configService != null) {
        try {
          log.info("[==SCG==] ready to shutdown dynamic-router s nacos config service instance ...");
          configService.shutDown();
        } catch (NacosException ignored) {
        }
      }
    }
  }

  // ~~ refresh definitions .
  @Deprecated
  private void refresh(@NonNull SCGRouteDefinition definition) {
    this.dynamicRouterService.refresh(definition);
  }

  private void refresh(List<SCGRouteDefinition> definitions) {
    this.dynamicRouterService.refresh(definitions);
  }
}
