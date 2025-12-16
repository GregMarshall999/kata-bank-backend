# Multi-stage Dockerfile for Kata Bank backend

# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven descriptor files first for better layer caching
COPY pom.xml ./
COPY Bank_Infrastructure/pom.xml Bank_Infrastructure/pom.xml
COPY Bank_User_Domain/pom.xml Bank_User_Domain/pom.xml
COPY Bank_Fund_Domain/pom.xml Bank_Fund_Domain/pom.xml
COPY Bank_Contact_Domain/pom.xml Bank_Contact_Domain/pom.xml
COPY Bank_Transaction_Domain/pom.xml Bank_Transaction_Domain/pom.xml

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY . ./

# Build all modules (infrastructure module depends on other domain modules)
RUN mvn -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy target directory from build stage, then extract JAR
COPY --from=build /app/Bank_Infrastructure/target/bank-infrastructure-*.jar app.jar

# Expose application port (overridable by SERVER_PORT env var)
EXPOSE 8801

# Use prod profile by default (can be overridden)
ENV SPRING_PROFILES_ACTIVE=prod

# Start the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
