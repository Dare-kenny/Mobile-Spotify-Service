# Use Java 17 runtime
FROM eclipse-temurin:17-jre

# Create app directory inside container
WORKDIR /app

# Copy your jar into the container
COPY app.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
