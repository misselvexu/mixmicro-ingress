## 如何接入网关

### 环境配置

环境 | 服务发现地址 | 服务发现(`Namespace`) | 网关地址 |备注
:----:|:----:|:----:|:----:|-----|
dev | [http://dev-middle.hgj.net:8848](http://dev-middle.hgj.net:8848) | `03a1c325-7c9b-41bf-b4f6-404a0cf22d5a` | http://dev-ingress.hgj.net | - |


### 新增网关配置

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

