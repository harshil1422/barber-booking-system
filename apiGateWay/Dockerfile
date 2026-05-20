

# Use lightweight Java image
FROM eclipse-temurin:17-jdk

# App name
ARG JAR_FILE=target/apiGateWay-0.0.1-SNAPSHOT.jar

# Copy jar
COPY ${JAR_FILE} app.jar

# Expose port (important for microservices)
EXPOSE 9090

# Run
ENTRYPOINT ["java","-jar","/app.jar"]