# Dockerfile for mixmicro-ingress-bootstrap
# Build with:
# docker build -t mixmicro-ingress-bootstrap .
# Run with:
# docker run -p 7777:7777 --rm -e SW_AGENT_COLLECTOR_BACKEND_SERVICES="10.10.10.72:11800" -e SERVER_ENV="dev" -v /tmp/logs:/tmp/logs -v /tmp/logs/mixmicro-ingress-bootstrap:/mixmicro-ingress-bootstrap/logs --name mixmicro-ingress-bootstrap mixmicro-ingress-bootstrap:1.0.0.RC3

FROM harbor.hgj.net/library/yunlsp-openjdk8:1.1
MAINTAINER Elve.Xu <iskp.me@gmail.com>

ARG PROJECT_VERSION
ARG PROJECT_NAME

#RUN echo "http://mirrors.aliyun.com/alpine/v3.6/main" > /etc/apk/repositories \
#    && echo "http://mirrors.aliyun.com/alpine/v3.6/community" >> /etc/apk/repositories \
#    && apk update upgrade \
#    && apk add --no-cache procps unzip curl bash tzdata \
#    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
#    && echo "Asia/Shanghai" > /etc/timezone

ADD ${PROJECT_NAME}-${PROJECT_VERSION}.zip /${PROJECT_NAME}/${PROJECT_NAME}-${PROJECT_VERSION}.zip

RUN unzip /${PROJECT_NAME}/${PROJECT_NAME}-${PROJECT_VERSION}.zip -d /${PROJECT_NAME} \
    && rm -rf /${PROJECT_NAME}/${PROJECT_NAME}-${PROJECT_VERSION}.zip

ENTRYPOINT ["/${PROJECT_NAME}/bin/startup.sh"]
