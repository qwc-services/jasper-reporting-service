# Jasper reports service
FROM sourcepole/qwc-uwsgi-base:alpine-latest

# Required for downloading jasper-reporting-service.jar
RUN apk add --no-cache --update wget
# Required for the service to run
RUN apk add --no-cache --update openjdk8 ttf-dejavu

ARG JASPER_SERVICE_URL
ARG AUTH_TOKEN

RUN mkdir /srv/qwc_service/ \
    mkdir /srv/qwc_service/config

RUN wget -O /srv/qwc_service/jasper-reporting-service.jar --header="PRIVATE-TOKEN: $AUTH_TOKEN" "$JASPER_SERVICE_URL"

WORKDIR /srv/qwc_service

# Run service
ENTRYPOINT ["java", "-DJava.awt.headless=true", "-jar", "jasper-reporting-service.jar"]
