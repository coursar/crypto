FROM maven:3-openjdk-16-slim AS build
WORKDIR /app
COPY . .
RUN mvn -B package

FROM openjdk:16-jdk-slim-buster
ARG VERSION=1.0

WORKDIR /app
COPY certs certs
COPY --from=build /app/target/crypto-${VERSION}-jar-with-dependencies.jar app.jar

EXPOSE 9999
CMD ["java", "-jar", "app.jar"]
