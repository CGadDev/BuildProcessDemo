# Use Java 17 as the runtime environment.
FROM eclipse-temurin:17-jdk

# Set the working directory inside the container.
WORKDIR /app

# Copy the JAR created by the Maven build process into the container.
COPY target/BuildProcessDemo-0.0.1-SNAPSHOT.jar app.jar

# Document the port used by the application.
EXPOSE 8081

# Start the Java application.
ENTRYPOINT ["java", "-jar", "app.jar"]