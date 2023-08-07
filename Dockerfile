# Use a base image with Java installed
FROM openjdk:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/chess-server-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Set the command to run the JAR file
CMD ["java", "-jar", "app.jar"]