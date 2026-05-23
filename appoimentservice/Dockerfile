FROM eclipse-temurin:17-jdk

ARG JAR_FILE=target/appointmentservice-0.0.1-SNAPSHOT.war

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]