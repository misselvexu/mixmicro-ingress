## 如何接入网关

### 环境配置

环境 | 服务发现地址 | 服务发现(`Namespace`) | 网关地址 |备注
:----:|:----:|:----:|:----:|-----|
dev | [http://dev-middle.hgj.net:8848](http://dev-middle.hgj.net:8848) | `03a1c325-7c9b-41bf-b4f6-404a0cf22d5a` | http://dev-ingress.hgj.net | - |
beta | [http://dev-middle.hgj.net:8848](http://dev-middle.hgj.net:8848) | `5c6451a6-181c-4838-9db0-f7f11bff47c7` | http://beta-ingress.hgj.net | - |


---
### `V1.2.X`版本配置

#### 新增网关配置

> 根据不同的环境隔离，访问对应的统一配置`Nacos`地址

-----
- `dev` 环境配置

**第一步:** 访问 [Dev Nacos Console](http://dev-middle.hgj.net:8848/nacos)

**第二步:** 左侧菜单栏选择 `配置管理` -> `配置列表` -> `dev namespace`

**第三步:** 选择编辑 `ingress-dynamic-router.json`

> 路由配置描述

```java

[
  {
    // 服务名称标识
    "id": "Mixmicro-Workshop-Server",
    // 加载查询顺序
    "order": 0,
    // 服务路径匹配 【必选】
    "predicates": [
      {
        "args": {
          // 匹配路径，遵循AntMatch规则
          "pattern": "/workshop/**"
        },
        // 固定拦截器名称
        "name": "Path"
      }
    ],
    // 服务拦截器配置
    "filters": [
      // StripPrefix 路径拦截 【必选】
      {
        "args": {
          // 固定写法
          "_genkey_0": "1"
        },
        // 固定拦截器名称
        "name": "StripPrefix"
      },

      // 重试拦截器， 【可选】，网关有默认的重试配合
      {
        // 重试拦截器固定名称
        "name": "Retry",
        // 重试配置
        "args": {
          // 重试次数，如配置2，默认重试次数为: 1+2 = 3
          "retries": "2",
          // 失败状态下进行重试，默认固定值
          "series": "SERVER_ERROR",
          // 指定重试的方法，默认是: GET，建议配置为：GET
          "methods": "GET",
          // 下游服务请求返回的状态代码，默认重试Http状态码：500、501、503、504
          "statuses": "INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT",
          // 下游服务请求异常状态，默认重试异常：IOException、TimeoutException
          "exceptions": "java.io.IOException, org.springframework.cloud.gateway.support.TimeoutException"
        }
      }
    ],
    "metadata": {
      // 服务超时时间配置 【可选】
      "connect-timeout": 2000,
      "response-timeout": 4000
    },
    // 服务负载均衡配置
    // 格式：'lb:xxx'      --> 标识普通的Http转发代理，依赖于服务注册中心Nacos
    // 格式：'lb:ws://xx'  --> 标识基于WebSocket转发配置，依赖于服务注册中心Nacos
    // 格式：'http(s)://xx.xxx.xxx.xx/'   --> 普通的Http服务转发，不依赖与服务注册中心Nacos
    "uri": "lb://Mixmicro-Workshop-Server"
  }
]

```

> 扩展配置

```yaml

##============================================================================##
##    Ingress Router Ext Config Template .                                    ##
##                                                                            ##
##    ## enabled flag                                                         ##
##    enabled: true                                                           ##
##                                                                            ##
##    ## black list config properties                                         ##
##    blackListConfig:                                                        ##
##      enabled: true                                                         ##
##      ## black items                                                        ##
##      items:                                                                ##
##        - enabled: true                                                     ##
##          requestUri: /**/swagger-ui.html                                   ##
##          limitFrom: '00:00:00'                                             ##
##          limitTo: '23:59:59'                                               ##
##          requestMethod: all                                                ##
##                                                                            ##
##      ## insensitive urls                                                   ##
##      insensitiveUrls:                                                      ##
##        - /**/actuator/health                                               ##
##============================================================================##

## enabled flag
enabled: true

## 拦截的黑名单URL
blackListConfig:
  enabled: true
  items:
    - requestUri: /**/swagger-ui.html
    - requestUri: /**/v2/api-docs
    - requestUri: /**/actuator/**
    - requestUri: /**/doc.html

  # 忽略拦截的URL
  insensitiveUrls:
    - /**/actuator/health
    - /**/actuator/prometheus


```


**第四步:** 点击完成`发布`即可



---

### `V1.0.X`版本配置

#### 新增网关配置

> 根据不同的环境隔离，访问对应的统一配置`Nacos`地址

-----
- `dev` 环境配置

**第一步:** 访问 [Dev Nacos Console](http://dev-middle.hgj.net:8848/nacos)

**第二步:** 左侧菜单栏选择 `配置管理` -> `配置列表` -> `dev namespace`

**第三步:** 选择编辑 `mixmicro-ingress-server-dev.properties`

配置描述

```properties
## 服务实例唯一标识定义
mixmicro.ingress.zuul.ext.routes.[服务标识].id=[服务标识]

## 实例访问路径配置, 格式: /xxx/xx/**
mixmicro.ingress.zuul.ext.routes.[服务标识].path=/xxx/**

## 实例标识配置，应用spring.application.name
mixmicro.ingress.zuul.ext.routes.[服务标识].service-id=[服务名称]

## 实例是否开启 Swagger 转发
mixmicro.ingress.zuul.ext.routes.[服务标识].swagger.enabled=true

## 实例Api版本配置
mixmicro.ingress.zuul.ext.routes.[服务标识].version=1.0.0
```

配置示例:

```properties
## 
mixmicro.ingress.zuul.ext.routes.ws.id=ws
mixmicro.ingress.zuul.ext.routes.ws.path=/ws/**
mixmicro.ingress.zuul.ext.routes.ws.service-id=Mixmicro-Workshop-Server
mixmicro.ingress.zuul.ext.routes.ws.swagger.enabled=true
mixmicro.ingress.zuul.ext.routes.ws.version=1.0.0
```

**第四步:** 点击完成`发布`即可


### 限流策略配置

选择编辑 `ingress-services-flow.json`

配置示例
```json

[   
  {
    "resource": "ws", // 资源名称标识
    "count": 1000     // MaxTPS数量
  }
]

```
