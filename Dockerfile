# Multi-stage build for Spring Boot application

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml files first for better layer caching
COPY pom.xml .
COPY Bank_User_Domain/pom.xml ./Bank_User_Domain/
COPY Bank_Infrastructure/pom.xml ./Bank_Infrastructure/
COPY Bank_Fund_Domain/pom.xml ./Bank_Fund_Domain/
COPY Bank_Contact_Domain/pom.xml ./Bank_Contact_Domain/
COPY Bank_Transaction_Domain/pom.xml ./Bank_Transaction_Domain/

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY Bank_User_Domain/src ./Bank_User_Domain/src
COPY Bank_Infrastructure/src ./Bank_Infrastructure/src
COPY Bank_Fund_Domain/src ./Bank_Fund_Domain/src
COPY Bank_Contact_Domain/src ./Bank_Contact_Domain/src
COPY Bank_Transaction_Domain/src ./Bank_Transaction_Domain/src

# Build the application (skip tests for faster builds, remove -DskipTests if you want to run tests)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the JAR file from build stage
COPY --from=build /app/Bank_Infrastructure/target/*.jar app.jar

# Expose the application port (default Spring Boot port)
EXPOSE 8080

# Health check (using curl which is available in jammy)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
