FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests


#RUN TIME

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN useradd --system --create-home bookinguser

COPY --from=build /app/target/*.jar app.jar

RUN chown -R bookinguser:bookinguser /app

USER bookinguser

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
