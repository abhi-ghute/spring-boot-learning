# Use a modern JDK base image
FROM openjdk:21-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR into the container
COPY target/RedisSpringBootMicroservice-0.0.1-SNAPSHOT.jar app.jar

# Document the port (optional but good practice)
EXPOSE 8080

# Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
