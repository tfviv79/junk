FROM node:24.21.0-trixie
ARG UID=2000
ARG GID=2000

RUN npm install -g @angular/cli
RUN groupadd -g $GID app && useradd -u $UID -g app app \
    && mkdir /home/app && chown -R app:app /home/app

RUN mkdir -p /opt/app
COPY front/init.sh /opt/app/init.sh

USER app
WORKDIR /opt/app/front

ENTRYPOINT ["bash", "/opt/app/init.sh"]
CMD ["loop"]

