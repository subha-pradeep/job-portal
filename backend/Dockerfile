# ---------- Stage 1: build the JAR ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Download dependencies first. This layer is cached until pom.xml changes,
# so rebuilding after a code change is much faster.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B package -DskipTests

# ---------- Stage 2: small runtime image ----------
# Only the JRE and the JAR, no Maven or source code: smaller and safer
FROM eclipse-temurin:21-jre
WORKDIR /app

# Don't run as root inside the container
RUN useradd --system --uid 1001 spring \
    && mkdir -p /app/uploads \
    && chown -R spring /app
USER spring

COPY --from=build /app/target/*.jar app.jar

ENV UPLOAD_DIR=/app/uploads
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
