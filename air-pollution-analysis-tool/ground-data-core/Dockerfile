FROM openjdk:15-jdk-alpine
MAINTAINER eipc16
ARG ARTIFACT_VERSION=latest
ENV ARTIFACT_PATH target/ground-data-core-$ARTIFACT_VERSION.jar
COPY $ARTIFACT_PATH ground-data-core-server.jar
ENTRYPOINT ["java","-jar","/ground-data-core-server.jar"]