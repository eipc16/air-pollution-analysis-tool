FROM openjdk:15-jdk-alpine
MAINTAINER eipc16
ARG ARTIFACT_VERSION=latest
ENV ARTIFACT_PATH target/main-data-processor-$ARTIFACT_VERSION.jar
COPY $ARTIFACT_PATH main-processor-server.jar
ENTRYPOINT ["java","-jar","/main-processor-server.jar"]