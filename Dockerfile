# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# Package stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/brokage-module-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 