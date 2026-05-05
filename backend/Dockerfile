# Generic Dockerfile reused for every Spring Boot service.
# Usage:  docker build --build-arg SERVICE=auth-service -f backend/Dockerfile -t vrs/auth-service backend/
ARG SERVICE
FROM maven:3.9-eclipse-temurin-17 AS build
ARG SERVICE
WORKDIR /app
COPY pom.xml .
COPY common-lib/pom.xml common-lib/pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY vehicle-service/pom.xml vehicle-service/pom.xml
COPY booking-service/pom.xml booking-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
RUN mvn -B -q -pl common-lib,${SERVICE} -am dependency:go-offline || true

COPY common-lib/src common-lib/src
COPY ${SERVICE}/src ${SERVICE}/src
RUN mvn -B -DskipTests -pl common-lib,${SERVICE} -am package

FROM eclipse-temurin:17-jre
ARG SERVICE
WORKDIR /app
COPY --from=build /app/${SERVICE}/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
