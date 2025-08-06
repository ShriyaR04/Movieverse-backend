# Use Eclipse Temurin JDK as the base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy project files
COPY . .

# Build the Spring Boot application
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Expose Spring Boot port
EXPOSE 8080

# Run the JAR file (replace with your actual JAR file name)
CMD ["java", "-jar", "target/Movieverse-0.0.1-SNAPSHOT.jar"]
