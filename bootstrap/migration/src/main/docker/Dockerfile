# Dockerfile for mixmicro-ingress-bootstrap-migration
# Build with:
# docker build -t mixmicro-ingress-bootstrap-migration .
# Run with:
# docker run -p 7777:7777 --rm -e SW_AGENT_COLLECTOR_BACKEND_SERVICES="10.10.10.72:11800" -e SERVER_ENV="dev" -v /tmp/logs:/tmp/logs -v /tmp/logs/mixmicro-ingress-bootstrap-migration:/mixmicro-ingress-bootstrap-migration/logs --name mixmicro-ingress-bootstrap-migration mixmicro-ingress-bootstrap-migration:1.0.0.RC3

FROM harbor.hgj.net/library/yunlsp-openjdk8:1.1
MAINTAINER Elve.Xu <iskp.me@gmail.com>

ENV VERSION 1.2.0.RELEASE

ADD mixmicro-ingress-bootstrap-migration-${VERSION}.zip /mixmicro-ingress-bootstrap-migration/mixmicro-ingress-bootstrap-migration-${VERSION}.zip

RUN unzip /mixmicro-ingress-bootstrap-migration/mixmicro-ingress-bootstrap-migration-${VERSION}.zip -d /mixmicro-ingress-bootstrap-migration \
    && rm -rf /mixmicro-ingress-bootstrap-migration/mixmicro-ingress-bootstrap-migration-${VERSION}.zip

ENTRYPOINT ["/mixmicro-ingress-bootstrap-migration/bin/startup.sh"]
