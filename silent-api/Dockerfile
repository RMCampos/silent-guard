### Builder
FROM ghcr.io/graalvm/native-image-community:21 AS build

# Copy
WORKDIR /app
COPY pom.xml mvnw ./
COPY src ./src
COPY .mvn/ ./.mvn

# Build
RUN ./mvnw -B package -Pnative -DskipTests

### Deployer
FROM gcr.io/distroless/java21-debian12:nonroot

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
      org.opencontainers.image.source="https://github.com/RMCampos/silent-guard"

# User, port and health check
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s CMD ["curl", "-f", "http://localhost:8080/actuator/health"]

# Startup
ENTRYPOINT ["/app/silentguardapi", "-Dspring.profiles.active=prod"]
