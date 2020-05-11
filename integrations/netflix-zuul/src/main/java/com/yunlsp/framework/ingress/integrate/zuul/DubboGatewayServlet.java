
package com.yunlsp.framework.ingress.integrate.zuul;

import com.alibaba.cloud.dubbo.metadata.DubboRestServiceMetadata;
import com.alibaba.cloud.dubbo.metadata.MethodParameterMetadata;
import com.alibaba.cloud.dubbo.metadata.RequestMetadata;
import com.alibaba.cloud.dubbo.metadata.RestMethodMetadata;
import com.alibaba.cloud.dubbo.metadata.repository.DubboServiceMetadataRepository;
import com.alibaba.cloud.dubbo.service.DubboGenericServiceFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.web.servlet.HttpServletBean;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * a simple dubbo gateway servlet
 *
 * @author 许路路
 */
@Slf4j
@WebServlet(name = "dubboGatewayServlet", urlPatterns = "/dsc/*")
public class DubboGatewayServlet extends HttpServletBean {

    private final DubboServiceMetadataRepository repository;

    private final DubboGenericServiceFactory serviceFactory;

    /**
     * 泛化服务缓存 已知的  dubbo 2.7.3  2.7.4.1 版本需要   dubbo 2.6.* 2.7.5  2.7.6不需要
     * 当前使用dubbo版本  2.7.4.1
     */
    private final Cache<DubboRestServiceMetadata, GenericService> restServiceCache =
            CacheBuilder.newBuilder()
                    .recordStats()
                    .maximumSize(2048)
                    .expireAfterAccess(24, TimeUnit.HOURS)
                    .build();

    private final Gson gson = new Gson();

    private final Map<String, Object> dubboTranslatedAttributes = new HashMap<>();

    public DubboGatewayServlet(DubboServiceMetadataRepository repository,
                               DubboGenericServiceFactory serviceFactory) {
        this.repository = repository;
        this.serviceFactory = serviceFactory;
        dubboTranslatedAttributes.put("protocol", "dubbo");
        dubboTranslatedAttributes.put("cluster", "failover");
    }

    private String resolveServiceName(HttpServletRequest request) {
        // /g/{app-name}/{rest-path}
        String requestURI = request.getRequestURI();
        // /g/
        String servletPath = request.getServletPath();

        String part = substringAfter(requestURI, servletPath);

        return substringBetween(part, "/", "/");
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        String serviceName = resolveServiceName(request);
        String restPath = substringAfter(request.getRequestURI(), serviceName);
        // 初始化 serviceName 的 REST 请求元数据
        repository.initializeMetadata(serviceName);
        // 将 HttpServletRequest 转化为 RequestMetadata
        RequestMetadata clientMetadata = buildRequestMetadata(request, restPath);
        DubboRestServiceMetadata dubboRestServiceMetadata = repository.get(serviceName,
                clientMetadata);
        if (dubboRestServiceMetadata == null) {
            // if DubboServiceMetadata is not found, executes next
            buildFail(response, "DubboServiceMetadata can't be found!" + serviceName, null);
            return;
        }
        RestMethodMetadata dubboRestMethodMetadata = dubboRestServiceMetadata
                .getRestMethodMetadata();
        //参数解析
        Pair<String[], Object[]> pair = resolveParam(request, clientMetadata, dubboRestMethodMetadata);
        String[] parameterTypes = pair.getLeft();
        Object[] args = pair.getRight();
        try {
            GenericService genericService = genericService(dubboRestServiceMetadata);
            Object result = genericService.$invoke(dubboRestMethodMetadata.getMethod().getName(),
                    parameterTypes, args);
            buildResponse(response, result);
            if (log.isDebugEnabled()) {
                log.debug("gateway invoke elapsed time {}ms.", (System.currentTimeMillis() - start));
            }
        } catch (GenericException e) {
            buildFail(response, "service invoke exception,path:" + restPath, e);
            log.warn("dubbo generic invoke exception,rest path:[{}],param types:[{}],params:[{}],ex:{}.",
                    restPath, parameterTypes, args, e);
        }
    }

    private void buildFail(HttpServletResponse response, String msg, Exception ex) throws IOException {
        response.setCharacterEncoding("UTF-8");
        if (StringUtils.isBlank(msg)) {
            msg = ex == null ? "system error" : ex.getMessage();
        }
        response.getWriter().println(msg);
    }

    private static final String GenericPojoFlag = "class";

    private void buildResponse(HttpServletResponse response, Object res) throws IOException {
        response.setCharacterEncoding("UTF-8");
        if (res instanceof Map) {
            Map<String, Object> resMap = (Map) res;
            if (resMap.get(GenericPojoFlag) != null) {
                resMap.remove(GenericPojoFlag);
            }
            res = gson.toJson(resMap);
            response.setContentType("application/json;charset=utf-8");
        }
        response.getWriter().println(res);
    }

    private Pair<String[], Object[]> resolveParam(HttpServletRequest request, RequestMetadata clientMetadata, RestMethodMetadata dubboRestMethodMetadata) throws IOException {
        String[] argTypes = new String[dubboRestMethodMetadata.getMethod().getParams().size()];
        Object[] args = new Object[dubboRestMethodMetadata.getMethod().getParams().size()];
        int index = 0;
        Iterator<MethodParameterMetadata> iterator = dubboRestMethodMetadata.getMethod().getParams().iterator();
        while (iterator.hasNext()) {
            MethodParameterMetadata parameterMetadata = iterator.next();
            argTypes[index] = parameterMetadata.getType();
            if (StringUtils.isNotBlank(dubboRestMethodMetadata.getBodyType()) && dubboRestMethodMetadata.getBodyIndex().equals(index)) {
                //body 参数解析
                Map<String, Object> data = gson.fromJson(request.getReader(), HashMap.class);
                args[index] = data;
            } else {
                // 基本类型解析
                Optional<String> optional = dubboRestMethodMetadata.getIndexToName().get(index).stream().findFirst();
                if (optional.isPresent()) {
                    String argName = optional.get();
                    args[index] = paramSwitch(clientMetadata.getParameter(argName), parameterMetadata.getType());
                }
            }
            index++;
        }
        return Pair.of(argTypes, args);
    }

    private synchronized GenericService genericService(DubboRestServiceMetadata dubboRestServiceMetadata) {
        GenericService genericService = restServiceCache.getIfPresent(dubboRestServiceMetadata);
        if (genericService == null) {
            genericService = serviceFactory.create(dubboRestServiceMetadata,
                    dubboTranslatedAttributes);
            restServiceCache.put(dubboRestServiceMetadata, genericService);
            log.info("cache miss or init,cache size:[{}],cache stat:[{}],service url:[{}]", restServiceCache.size(),
                    restServiceCache.stats(), dubboRestServiceMetadata.getServiceRestMetadata().getUrl());
        }
        return genericService;
    }

    private RequestMetadata buildRequestMetadata(HttpServletRequest request,
                                                 String restPath) {
        RequestMetadata requestMetadata = new RequestMetadata();
        requestMetadata.setPath(restPath);
        requestMetadata.setMethod(request.getMethod());
        requestMetadata.setParams(getParams(request));
        requestMetadata.setHeaders(getHeaders(request));
        return requestMetadata;
    }

    private Object paramSwitch(String value, String valueType) {
        if (value == null) {
            return null;
        }
        if (String.class.getName().equals(valueType)) {
            return String.valueOf(value);
        } else if (Integer.class.getName().equals(valueType) || int.class.getName().equals(valueType)) {
            return Integer.valueOf(value);
        } else if (Byte.class.getName().equals(valueType) || byte.class.getName().equals(valueType)) {
            return Byte.valueOf(value);
        } else if (Long.class.getName().equals(valueType) || long.class.getName().equals(valueType)) {
            return Long.valueOf(value);
        } else if (Double.class.getName().equals(valueType) || double.class.getName().equals(valueType)) {
            return Double.valueOf(value);
        } else if (Float.class.getName().equals(valueType) || float.class.getName().equals(valueType)) {
            return Float.valueOf(value);
        } else if (Character.class.getName().equals(valueType) || char.class.getName().equals(valueType)) {
            return value;
        } else if (Short.class.getName().equals(valueType) || short.class.getName().equals(valueType)) {
            return Short.valueOf(value);
        } else if (Boolean.class.getName().equals(valueType) || boolean.class.getName().equals(valueType)) {
            return Boolean.valueOf(value);
        } else if (List.class.getName().equals(valueType)) {
            return new ArrayList<>(Arrays.asList(value));
        }
        return value;
    }

    private Map<String, List<String>> getHeaders(HttpServletRequest request) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            map.put(headerName, Collections.list(headerValues));
        }
        return map;
    }

    private Map<String, List<String>> getParams(HttpServletRequest request) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            map.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }
        return map;
    }

}
