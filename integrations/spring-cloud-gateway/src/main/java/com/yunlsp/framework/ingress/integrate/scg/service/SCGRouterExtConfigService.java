package com.yunlsp.framework.ingress.integrate.scg.service;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;
import com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigBean;
import com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigBean.Type.NACOS;
import static com.yunlsp.framework.ingress.integrate.scg.SCGRouterConfigBeanContext.context;

/**
 * {@link SCGRouterExtConfigService}
 *
 * <p>Class SCGRouterExtConfigService Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/23
 */
public class SCGRouterExtConfigService implements DisposableBean {

  private static final Logger log = LoggerFactory.getLogger(SCGRouterExtConfigService.class);

  /**
   * Nacos DataSource Config Instance
   *
   * <p>
   */
  private final SCGRouterConfigProperties properties;

  public SCGRouterExtConfigService(SCGRouterConfigProperties properties) {
    this.properties = properties;
  }

  // ~~
  private ConfigService configService;

  private static final AtomicBoolean configServiceInitialized = new AtomicBoolean();

  // ~~

  public void initialize() {

    try{

      if(configServiceInitialized.compareAndSet(false, true)) {

        if( !Objects.equals(NACOS, properties.getType())) {
          log.warn("[==SCG==] supported router ext config datasource type is not nacos .");
          return;
        }

        SCGRouterConfigProperties.NacosDataSource nacosDataSource = properties.getNacos();

        log.info("[==SCG==] initialize scg router ext config service ...");

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESPACE, nacosDataSource.getNamespace());
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosDataSource.getServerAddr());

        configService = new NacosConfigService(properties);

        log.info("[==SCG==] router ext config service is created , instance: {}", configService);

        String content = configService.getConfigAndSignListener(
            nacosDataSource.getDataId(),
            nacosDataSource.getDataGroup(),
            nacosDataSource.getDefaultTimeout(),
            new AbstractListener() {

              @Override
              public void receiveConfigInfo(String configInfo) {

                try{
                  log.info("[==SCG==] <<< received router ext config content : \r\n<<<<<<< HEAD \r\n {} \r\n--EOF-- \r\n", configInfo);
                  SCGRouterConfigBean bean = SCGRouterConfigBean.load(configInfo);

                  context().refresh(bean);

                } catch (Exception e) {
                  log.warn("[==SCG==] process nacos server pushed config stream failed", e);
                }

              }
            });

        log.info("[==SCG==] >>> load router ext content : \r\n {}", content);

        // refresh after first initialized .
        SCGRouterConfigBean bean = SCGRouterConfigBean.load(content);

        context().refresh(bean);

        log.info("[==SCG==] router is refreshed ~");
      }

    } catch (Exception e) {
      log.warn("[==SCG==] nacos config service initialize failed .", e);
    }
  }


  // ~~ destroy

  @Override
  public void destroy() {
    if(configServiceInitialized.compareAndSet(true, false)) {
      if(configService != null) {
        try {
          log.info("[==SCG==] ready to shutdown router ext's nacos config service instance ...");
          configService.shutDown();
        } catch (NacosException ignored) {
        }
      }
    }
  }
}
