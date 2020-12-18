package com.yunlsp.framework.ingress.plugin.router.context;

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
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

  public SCGDynamicRouterService(RouteDefinitionWriter routeDefinitionWriter) {
    this.routeDefinitionWriter = routeDefinitionWriter;
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

  public void refresh(SCGRouteDefinition sourceDefinition) {
    RouteDefinition definition = assembleRouteDefinition(sourceDefinition);
    this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
    this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
    this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
  }

  public void remove(String id) {
    this.routeDefinitionWriter.delete(Mono.just(id));
  }


  // ~~ parse

  private RouteDefinition assembleRouteDefinition(@NonNull SCGRouteDefinition sourceDefinition) {
    RouteDefinition definition = new RouteDefinition();
    List<PredicateDefinition> pdList = new ArrayList<>();
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
    for(SCGFilterDefinition filterDefinition : gatewayFilters){
      FilterDefinition filter = new FilterDefinition();
      filter.setName(filterDefinition.getName());
      filter.setArgs(filterDefinition.getArgs());
      filters.add(filter);
    }
    definition.setFilters(filters);

    URI uri;
    if(sourceDefinition.getUri().startsWith("http")){
      uri = UriComponentsBuilder.fromHttpUrl(sourceDefinition.getUri()).build().toUri();
    }else{
      uri = URI.create(sourceDefinition.getUri());
    }

    definition.setUri(uri);
    return definition;
  }
}
