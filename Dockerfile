# Jasper reports service
FROM sourcepole/qwc-uwsgi-base:alpine-v2021.12.16

# Required for downloading jasper-reporting-service.jar
RUN apk add --no-cache --update wget
# Required for the service to run
RUN apk add --no-cache --update openjdk8 ttf-dejavu

ARG JASPER_SERVICE_URL
ARG AUTH_TOKEN

RUN mkdir -p /srv/jasper-reporting-service/config

WORKDIR /srv/jasper-reporting-service

RUN wget -O jasper-reporting-service.jar --header="PRIVATE-TOKEN: $AUTH_TOKEN" "$JASPER_SERVICE_URL"

# Run service
ENTRYPOINT ["java", "-DJava.awt.headless=true", "-jar", "jasper-reporting-service.jar"]
