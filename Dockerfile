FROM sourcepole/qwc-uwsgi-base:alpine-v2023.10.26

ARG JASPER_SERVICE_URL
ARG AUTH_TOKEN

RUN mkdir -p /srv/jasper-reporting-service/config

WORKDIR /srv/jasper-reporting-service

RUN \
    apk add --no-cache --update --virtual runtime-deps openjdk8 ttf-dejavu && \
    apk add --no-cache --update --virtual build-deps wget && \
    wget -O jasper-reporting-service.jar --header="PRIVATE-TOKEN: $AUTH_TOKEN" "$JASPER_SERVICE_URL" && \
    apk del build-deps

# Run service
ENTRYPOINT ["java", "-DJava.awt.headless=true", "-jar", "jasper-reporting-service.jar"]
