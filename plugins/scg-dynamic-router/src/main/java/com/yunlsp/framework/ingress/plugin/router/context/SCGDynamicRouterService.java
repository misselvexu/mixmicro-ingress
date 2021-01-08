package com.yunlsp.framework.ingress.plugin.router.context;

import com.yunlsp.framework.ingress.plugin.router.SCGDefaultRetryProperties;
import com.yunlsp.framework.ingress.plugin.router.core.model.SCGFilterDefinition;
import com.yunlsp.framework.ingress.plugin.router.core.model.SCGPredicateDefinition;
import com.yunlsp.framework.ingress.plugin.router.core.model.SCGRouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yunlsp.framework.ingress.plugin.router.SCGDefaultRetryProperties.RETRY_FILTER_NAME;

/**
 * {@link SCGDynamicRouterService}
 *
 * <p>Class SCGDynamicRouterService Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/19
 */
public class SCGDynamicRouterService implements ApplicationEventPublisherAware {

  private static final Logger log = LoggerFactory.getLogger(SCGDynamicRouterService.class);

  private ApplicationEventPublisher applicationEventPublisher;

  private final RouteDefinitionWriter routeDefinitionWriter;

  private final Object lock = new Object();

  private final SCGDefaultRetryProperties properties;

  public SCGDynamicRouterService(SCGDefaultRetryProperties properties, RouteDefinitionWriter routeDefinitionWriter) {
    this.routeDefinitionWriter = routeDefinitionWriter;
    this.properties = properties;
  }

  /**
   * Set the ApplicationEventPublisher that this object runs in.
   *
   * <p>Invoked after population of normal bean properties but before an init callback like
   * InitializingBean's afterPropertiesSet or a custom init-method. Invoked before
   * ApplicationContextAware's setApplicationContext.
   *
   * @param applicationEventPublisher event publisher to be used by this object
   */
  @Override
  public void setApplicationEventPublisher(
      @NonNull ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  // ~~ RouteDefinition operations .

  public void add(SCGRouteDefinition sourceDefinition) {
    RouteDefinition definition = assembleRouteDefinition(sourceDefinition);
    this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
    this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
  }

  @Deprecated
  public void refresh(SCGRouteDefinition sourceDefinition) {
    RouteDefinition definition = assembleRouteDefinition(sourceDefinition);
    this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
    this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
    this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
  }

  public void refresh(List<SCGRouteDefinition> sourceDefinitions) {

    synchronized (lock) {
      Flux.fromIterable(sourceDefinitions)
          .subscribe(
              sourceDefinition -> {
                RouteDefinition definition = assembleRouteDefinition(sourceDefinition);
                this.routeDefinitionWriter.delete(Mono.just(sourceDefinition.getId()));
                this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
              });

      this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }
  }

  public void remove(String id) {
    this.routeDefinitionWriter.delete(Mono.just(id));
  }


  // ~~ parse

  private RouteDefinition assembleRouteDefinition(@NonNull SCGRouteDefinition sourceDefinition) {
    RouteDefinition definition = new RouteDefinition();
    List<PredicateDefinition> pdList = new ArrayList<>();
    definition.setOrder(sourceDefinition.getOrder());
    definition.setId(sourceDefinition.getId());
    List<SCGPredicateDefinition> gatewayPredicateDefinitionList = sourceDefinition.getPredicates();
    for (SCGPredicateDefinition temp : gatewayPredicateDefinitionList) {
      PredicateDefinition predicate = new PredicateDefinition();
      predicate.setArgs(temp.getArgs());
      predicate.setName(temp.getName());
      pdList.add(predicate);
    }
    definition.setPredicates(pdList);

    //设置过滤器
    List<FilterDefinition> filters = new ArrayList<>();
    List<SCGFilterDefinition> gatewayFilters = sourceDefinition.getFilters();

    boolean hasRetryFilter = false;

    for(SCGFilterDefinition filterDefinition : gatewayFilters){
      if(Objects.equals(RETRY_FILTER_NAME, filterDefinition.getName())) {
        hasRetryFilter = true;
      }

      FilterDefinition filter = new FilterDefinition();
      filter.setName(filterDefinition.getName());
      filter.setArgs(filterDefinition.getArgs());
      filters.add(filter);
    }

    // check default retry config .
    if(!hasRetryFilter && properties.isEnabled()) {
      FilterDefinition filterDefinition = new FilterDefinition();
      filterDefinition.setName(RETRY_FILTER_NAME);
      filterDefinition.setArgs(properties.getArgs());
      filters.add(filterDefinition);
    }

    definition.setFilters(filters);

    URI uri;
    if(sourceDefinition.getUri().startsWith("http")){
      uri = UriComponentsBuilder.fromHttpUrl(sourceDefinition.getUri()).build().toUri();
    }else{
      /*
       * URI issues , lb service name un-support underscore
       *
       * Reference Spring Framework Issues:  https://github.com/spring-projects/spring-framework/issues/24439
       * Reference Spring Cloud Common Issues:  https://github.com/spring-cloud/spring-cloud-commons/issues/159
       */
//      uri = UriComponentsBuilder.fromUriString(sourceDefinition.getUri()).build().toUri();
      uri = URI.create(sourceDefinition.getUri());
    }

    definition.setUri(uri);
    return definition;
  }
}
