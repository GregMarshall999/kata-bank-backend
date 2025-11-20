# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy the complete Maven project. The .dockerignore file keeps build output out
# of the build context so that the image stays small.
COPY . .

# Build only the infrastructure module (which wires the other modules) to save time.
RUN mvn -B -ntp -pl "Bank Infrastructure" -am package -DskipTests

# Normalise the output jar path (the module folder contains spaces).
RUN set -eux; \
    JAR_FILE="$(find 'Bank Infrastructure/target' -maxdepth 1 -type f -name '*.jar' \
        ! -name '*-sources.jar' \
        ! -name '*-javadoc.jar' \
        ! -name '*-tests.jar' \
        ! -name '*-plain.jar' \
        | head -n 1)"; \
    test -n "$JAR_FILE" || { echo "Could not locate the packaged application jar" >&2; exit 1; }; \
    cp "$JAR_FILE" /workspace/app.jar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=build /workspace/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]

