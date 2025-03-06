# Use the official OpenJDK 21 image as the base image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build directory into the container
COPY build/libs/hobby-0.0.1.jar /app/hobby-0.0.1.jar

# Expose the port the app runs on (adjust as necessary)
EXPOSE 8080

# Run the JAR file when the container starts
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "/app/hobby-0.0.1.jar"]