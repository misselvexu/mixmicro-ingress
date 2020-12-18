# Dockerfile for mixmicro-ingress-bootstrap-scg
# Build with:
# docker build -t mixmicro-ingress-bootstrap-scg .
# Run with:
# docker run -p 7777:7777 --rm -e SW_AGENT_COLLECTOR_BACKEND_SERVICES="10.10.10.72:11800" -e SERVER_ENV="dev" -v /tmp/logs:/tmp/logs -v /tmp/logs/mixmicro-ingress-bootstrap-scg:/mixmicro-ingress-bootstrap-scg/logs --name mixmicro-ingress-bootstrap-scg mixmicro-ingress-bootstrap-scg:1.0.0.RC3

FROM harbor.hgj.net/library/yunlsp-openjdk8:1.1
MAINTAINER Elve.Xu <iskp.me@gmail.com>

ENV VERSION 1.2.0.BUILD-SNAPSHOT

ADD mixmicro-ingress-bootstrap-scg-${VERSION}.zip /mixmicro-ingress-bootstrap-scg/mixmicro-ingress-bootstrap-scg-${VERSION}.zip

RUN unzip /mixmicro-ingress-bootstrap-scg/mixmicro-ingress-bootstrap-scg-${VERSION}.zip -d /mixmicro-ingress-bootstrap-scg \
    && rm -rf /mixmicro-ingress-bootstrap-scg/mixmicro-ingress-bootstrap-scg-${VERSION}.zip

ENTRYPOINT ["/mixmicro-ingress-bootstrap-scg/bin/startup.sh"]
