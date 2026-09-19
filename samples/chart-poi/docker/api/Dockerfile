FROM amazoncorretto:21.0.12
ARG UID=2000
ARG GID=2000

RUN dnf install -y shadow-utils findutils procps
RUN dnf clean all

RUN groupadd -g $GID app && useradd -u $UID -g app app
RUN mkdir -p /opt/app
COPY api/init.sh /opt/app/init.sh
USER app
WORKDIR /opt/app/api

ENTRYPOINT ["bash", "/opt/app/init.sh"]
CMD ["loop"]
