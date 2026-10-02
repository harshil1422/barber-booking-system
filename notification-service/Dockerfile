# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY ["pom.xml", "/app/"]
RUN mvn dependency:go-offline -B

COPY ["src/", "/app/src/"]
RUN mvn clean package -DskipTests


# Runtime stage
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

RUN useradd --system --create-home notificationuser

COPY --from=build --chown=notificationuser:notificationuser ["/app/target/notification-service-0.0.1-SNAPSHOT.jar", "/app/app.jar"]

USER notificationuser

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]