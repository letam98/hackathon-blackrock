FROM eclipse-temurin:21-jdk-ubi9-minimal
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 5477
ENTRYPOINT ["java", "-jar", "app.jar"]