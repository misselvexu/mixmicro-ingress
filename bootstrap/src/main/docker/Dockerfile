# Dockerfile for mixmicro-ingress-bootstrap
# Build with:
# docker build -t mixmicro-ingress-bootstrap .
# Run with:
# docker run -p 7777:7777 --rm -e SW_AGENT_COLLECTOR_BACKEND_SERVICES="10.10.10.72:11800" -e SERVER_ENV="dev" -v /tmp/logs:/tmp/logs -v /tmp/logs/mixmicro-ingress-bootstrap:/mixmicro-ingress-bootstrap/logs --name mixmicro-ingress-bootstrap mixmicro-ingress-bootstrap:1.0.0.RC3

FROM harbor.hgj.net/library/yunlsp-openjdk8:1.1
MAINTAINER Elve.Xu <iskp.me@gmail.com>

ENV VERSION 1.0.2.RC3

ADD mixmicro-ingress-bootstrap-${VERSION}.zip /mixmicro-ingress-bootstrap/mixmicro-ingress-bootstrap-${VERSION}.zip

RUN unzip /mixmicro-ingress-bootstrap/mixmicro-ingress-bootstrap-${VERSION}.zip -d /mixmicro-ingress-bootstrap \
    && rm -rf /mixmicro-ingress-bootstrap/mixmicro-ingress-bootstrap-${VERSION}.zip

ENTRYPOINT ["/mixmicro-ingress-bootstrap/bin/startup.sh"]
