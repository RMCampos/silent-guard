### Builder
FROM ghcr.io/graalvm/native-image-community:21 AS build

# Copy
WORKDIR /app
COPY pom.xml mvnw ./
COPY src ./src
COPY .mvn/ ./.mvn

# Build
RUN ./mvnw -B package -Pnative -DskipTests -Dspring.profiles.active=prod

### Deployer
FROM debian:12.11-slim AS deploy
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Copy
WORKDIR /app
COPY --from=build /app/target/silentguardapi ./silentguardapi

ARG BUILD
ARG SOURCE_PR
ENV BUILD=${BUILD}
ENV SOURCE_PR=${SOURCE_PR}

# Add metadata to the final image
LABEL org.opencontainers.image.authors="Ricardo Campos <ricardompcampos@gmail.com>" \
      org.opencontainers.image.vendor="Ricardo Campos Org" \
      org.opencontainers.image.title="Silent Guard API" \
      org.opencontainers.image.description="Spring Cloud Native REST API service" \
      org.opencontainers.image.version="${SOURCE_PR}" \
      org.opencontainers.image.source="https://github.com/rmcampos/silent-guard"

# User, port and health check
USER 1001
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD ["curl", "-f", "http://localhost:8080/actuator/health"]

# Startup
ENTRYPOINT ["/app/silentguardapi", "-Dspring.profiles.active=prod"]
