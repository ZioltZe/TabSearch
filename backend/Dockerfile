FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/backend.p12 backend.p12
ENTRYPOINT ["java", "-jar", "app.jar"]
