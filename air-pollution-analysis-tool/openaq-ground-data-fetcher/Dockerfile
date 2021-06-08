FROM openjdk:15-jdk-alpine
MAINTAINER eipc16
ARG ARTIFACT_VERSION=latest
ENV ARTIFACT_PATH target/openaq-ground-data-fetcher-$ARTIFACT_VERSION.jar
COPY $ARTIFACT_PATH openaq-adapter-server.jar
ENTRYPOINT ["java","-jar","/openaq-adapter-server.jar"]