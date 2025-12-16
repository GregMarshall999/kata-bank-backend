# Multi-stage Dockerfile for Kata Bank backend

# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven descriptor files first for better layer caching
COPY pom.xml ./
COPY ["Bank Infrastructure/pom.xml", "Bank Infrastructure/pom.xml"]
COPY ["Bank User Domain/pom.xml", "Bank User Domain/pom.xml"]
COPY ["Bank Fund Domain/pom.xml", "Bank Fund Domain/pom.xml"]
COPY ["Bank Contact Domain/pom.xml", "Bank Contact Domain/pom.xml"]
COPY ["Bank Transaction Domain/pom.xml", "Bank Transaction Domain/pom.xml"]

# Download dependencies
RUN mvn -q dependency:go-offline

# Copy source code
COPY . ./

# Build only the infrastructure module which contains the Spring Boot app
RUN mvn -q -DskipTests package -pl 'Bank Infrastructure' -am

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy target directory from build stage, then extract JAR
COPY --from=build ["/app/Bank Infrastructure/target", "/tmp/target"]
RUN cp /tmp/target/bank-infrastructure-*.jar /app/app.jar && rm -rf /tmp/target

# Expose application port (overridable by SERVER_PORT env var)
EXPOSE 8801

# Use prod profile by default (can be overridden)
ENV SPRING_PROFILES_ACTIVE=prod

# Start the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
