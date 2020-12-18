# Dockerfile for mixmicro-ingress-bootstrap-zuul
# Build with:
# docker build -t mixmicro-ingress-bootstrap-zuul .
# Run with:
# docker run -p 7777:7777 --rm -e SW_AGENT_COLLECTOR_BACKEND_SERVICES="10.10.10.72:11800" -e SERVER_ENV="dev" -v /tmp/logs:/tmp/logs -v /tmp/logs/mixmicro-ingress-bootstrap-zuul:/mixmicro-ingress-bootstrap-zuul/logs --name mixmicro-ingress-bootstrap-zuul mixmicro-ingress-bootstrap-zuul:1.0.0.RC3

FROM harbor.hgj.net/library/yunlsp-openjdk8:1.1
MAINTAINER Elve.Xu <iskp.me@gmail.com>

ENV VERSION 1.2.0.BUILD-SNAPSHOT

ADD mixmicro-ingress-bootstrap-zuul-${VERSION}.zip /mixmicro-ingress-bootstrap-zuul/mixmicro-ingress-bootstrap-zuul-${VERSION}.zip

RUN unzip /mixmicro-ingress-bootstrap-zuul/mixmicro-ingress-bootstrap-zuul-${VERSION}.zip -d /mixmicro-ingress-bootstrap-zuul \
    && rm -rf /mixmicro-ingress-bootstrap-zuul/mixmicro-ingress-bootstrap-zuul-${VERSION}.zip

ENTRYPOINT ["/mixmicro-ingress-bootstrap-zuul/bin/startup.sh"]
