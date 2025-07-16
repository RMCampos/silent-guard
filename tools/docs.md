# Tools Documentation

## Overview

The `tools/` directory contains documentation, commands, and useful resources for developing, building, and deploying the Silent Guard application. This serves as a reference for developers working on the project, providing quick access to commonly used commands, deployment procedures, and helpful links.

## Contents

### tools.md
A comprehensive reference document containing:
- **Build Commands**: Docker build procedures for both frontend and backend
- **Deployment Procedures**: Manual deployment steps and automation
- **Development Tools**: Docker commands for running services
- **Useful Links**: External resources and tracking tools

## Build Procedures

### Frontend Web App (React)

#### Docker Build
```bash
docker build --no-cache \
 --build-arg VITE_BUILD="v999-$(date '+%Y-%m-%d-%H%M%S')" \
 --build-arg SOURCE_PR="v999-123456789-$(date '+%Y-%m-%d-%H%M%S')" \
 -t client:candidate \
 ./silent-app
```

**Build Arguments**:
- **VITE_BUILD**: Version identifier with timestamp for tracking
- **SOURCE_PR**: Pull request identifier with timestamp for traceability

**Features**:
- Uses multi-stage Docker build for optimization
- Includes build version injection for runtime identification
- No-cache flag ensures fresh builds with latest dependencies

### Backend REST API (Spring Boot)

#### Standard Docker Build
```bash
docker build --no-cache \
 --build-arg BUILD="v999-$(date '+%Y-%m-%d-%H%M%S')" \
 --build-arg SOURCE_PR="v999-123456789-$(date '+%Y-%m-%d-%H%M%S')" \
 -t silent-guard-api:candidate \
 ./silent-api
```

#### Fat JAR Build
Build a standalone JAR with all dependencies included:
```bash
./mvnw package spring-boot:repackage -DskipTests
```

**Output**: `silentguardapi-0.0.1-SNAPSHOT.jar` in `target/` directory

#### GraalVM Native Image Build
Build cloud-native executable with GraalVM:
```bash
./mvnw -B package -Pnative -DskipTests
```

**Features**:
- Faster startup time
- Lower memory footprint
- Ahead-of-time compilation
- Cloud-native deployment ready

#### GraalVM Agent for Native Hints
Generate native image configuration hints:
```bash
java -jar \
  -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image \
  silentguardapi-0.0.1-SNAPSHOT.jar
```

**Purpose**: Generates reflection and resource configuration for native compilation

## Development Environment

### Running Services with Docker

#### Database (PostgreSQL)
```bash
docker run -d -p 5432:5432 --rm \
  --name silent-db \
  -e POSTGRES_DB=$POSTGRES_DB \
  -e POSTGRES_USER=$POSTGRES_USER \
  -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
  postgres:15.8-bookworm
```

**Configuration**:
- **Port**: 5432 (standard PostgreSQL port)
- **Image**: PostgreSQL 15.8 on Debian Bookworm
- **Auto-remove**: Container cleaned up on stop

#### API (Spring Boot)
```bash
docker run -p 8080:8080 --rm \
  --name silent-api \
  -e AUTH_DOMAIN=$AUTH_DOMAIN \
  -e API_IDENTIFIER=$API_IDENTIFIER \
  -e POSTGRES_DB=$POSTGRES_DB \
  -e POSTGRES_USER=$POSTGRES_USER \
  -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
  -e POSTGRES_PORT=$POSTGRES_PORT \
  -e POSTGRES_HOST=$POSTGRES_HOST \
  -e CORS_ALLOWED_ORIGINS=$CORS_ALLOWED_ORIGINS \
  -e MAILGUN_APIKEY=$MAILGUN_APIKEY \
  -e TARGET_ENV=$TARGET_ENV \
  -e API_LOGGING_LEVEL=$API_LOGGING_LEVEL \
  silent-guard-api:candidate
```

**Environment Variables Required**:
- **AUTH_DOMAIN**: Auth0 domain for JWT validation
- **API_IDENTIFIER**: Auth0 API identifier
- **Database Configuration**: Connection details for PostgreSQL
- **CORS_ALLOWED_ORIGINS**: Frontend domains for CORS policy
- **MAILGUN_APIKEY**: Email service API key
- **TARGET_ENV**: Environment mode (development/production)
- **API_LOGGING_LEVEL**: Logging verbosity

#### Frontend (React/Nginx)
The frontend runs on Nginx after build. Configuration handled through environment variables injected at build time.

## Manual Deployment Procedures

### Deployment Steps
1. **Connect to Server**: SSH into deployment server
2. **Run Deploy Scripts**: Execute automated deployment procedures

### Frontend Deployment Example
```bash
# Set deployment variables
export SERVER_IP=your.server.ip
export SERVER_ADDRESS=your.domain.com
export VERSION=v1.0.0

# Install dependencies and build
npm ci --ignore-scripts --no-update-notifier --omit=dev
export VITE_BACKEND_SERVER=$SERVER_ADDRESS/server
npm run build

# Package and deploy
zip -r "client_$VERSION.zip" dist/
scp "client_$VERSION.zip" root@$SERVER_IP:/root/
```

**Process**:
1. **Clean Install**: Install only production dependencies
2. **Environment Configuration**: Set backend server URL
3. **Build**: Create production build
4. **Package**: Create deployment archive
5. **Transfer**: Upload to server via SCP

## Container Registry Integration

### GitHub Container Registry (GHCR)

#### Authentication
```bash
export CR_PAT=YOUR_GITHUB_TOKEN
echo $CR_PAT | docker login ghcr.io -u rmcampos --password-stdin
```

#### Pulling Images
```bash
docker pull ghcr.io/ricardo-campos-org/react-typescript-todolist/client:50
docker pull ghcr.io/ricardo-campos-org/react-typescript-todolist/server:50
```

#### Pushing Images
```bash
docker push ghcr.io/ricardo-campos-org/react-typescript-todolist/server:316
```

**Features**:
- **Versioned Images**: Tag-based version management
- **Automated Builds**: CI/CD integration for automatic pushes
- **Access Control**: Private repository support

## Docker Utilities

### Container Inspection

#### Get Container IP Address
```bash
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' db
```

#### Health Check Monitoring
```bash
# Single check
docker inspect --format "{{json .State.Health}}" container_name_or_id | jq

# Continuous monitoring
while true; do 
  clear
  docker inspect --format "{{json .State.Health}}" server | jq
  sleep 1
done
```

#### Extract Environment Variables
```bash
# Get specific environment variable from image
docker inspect client:candidate | \
  jq -r '.[0].Config.Env[] | select(startswith("SOURCE_PR="))' | \
  sed -n 's/SOURCE_PR=\(v[0-9]*\).*/\1/p'
```

## Database Management

### Development Database Restoration

#### Start PostgreSQL Container
```bash
docker run \
 --name my-postgres-db \
 -e POSTGRES_USER="<user>" \
 -e POSTGRES_PASSWORD='<password>' \
 -e POSTGRES_DB="<db-name>" \
 -p 5432:5432 \
 -d postgres
```

#### Restore from Backup
```bash
docker exec -i my-postgres-db pg_restore -U <user> -d <db-name> < 2025-04-26T20_00_00.114Z.sql
```

**Use Cases**:
- **Testing**: Restore production data for testing
- **Development**: Seed development environment
- **Debugging**: Reproduce production issues locally

## External Resources

### Useful Links
- **GitHub Container Registry**: https://ghcr.io/
  - Container image hosting and management
  - Integration with GitHub Actions for automated builds

- **Time Tracking**: https://track.toggl.com/timer
  - Project time tracking for development hours
  - Task-based time management

### Development Tools
- **Docker Desktop**: Container management UI
- **GitHub Actions**: CI/CD automation
- **PostgreSQL Admin Tools**: pgAdmin, DBeaver for database management
- **API Testing**: Bruno, Postman for API development
- **Monitoring**: Docker stats, container logs for debugging

## Responsive Design Testing

### Bootstrap Breakpoint Visualization
The tools.md file includes a helpful HTML snippet for testing responsive breakpoints:

```html
<div className="py-5 text-center">
  <div className="d-block d-sm-none bg-primary text-white p-3">XS (Extra Small): less than 576px</div>
  <div className="d-none d-sm-block d-md-none bg-secondary text-white p-3">SM (Small): ≥576px</div>
  <div className="d-none d-md-block d-lg-none bg-success text-white p-3">MD (Medium): ≥768px</div>
  <div className="d-none d-lg-block d-xl-none bg-warning text-dark p-3">LG (Large): ≥992px</div>
  <div className="d-none d-xl-block d-xxl-none bg-danger text-white p-3">XL (Extra Large): ≥1200px</div>
  <div className="d-none d-xxl-block bg-dark text-white p-3">XXL (Extra Extra Large): ≥1400px</div>
</div>
```

**Purpose**: Visual indicator of current screen size during responsive design testing

## Performance Optimization

### Docker Layer Caching
- Use `.dockerignore` files to exclude unnecessary files
- Order Dockerfile commands from least to most frequently changing
- Use multi-stage builds for smaller production images

### Build Optimization
- **No-cache builds**: Ensure latest dependencies and security updates
- **Parallel builds**: Build frontend and backend simultaneously
- **Image cleanup**: Regular pruning of unused images and containers

## Security Considerations

### Container Security
- **Base Images**: Use official, maintained base images
- **Vulnerability Scanning**: Regular security scans of container images
- **Secrets Management**: Environment variables for sensitive data
- **Network Security**: Proper container networking and isolation

### Deployment Security
- **HTTPS**: SSL/TLS for all production traffic
- **Authentication**: Proper Auth0 configuration
- **CORS**: Restrictive CORS policies
- **Database Security**: Encrypted connections and secure credentials

## Troubleshooting

### Common Issues

#### Build Failures
- **Node.js Version**: Ensure compatible Node.js version for frontend builds
- **Java Version**: Verify Java 21 for backend builds
- **Docker Resources**: Sufficient memory and disk space for builds

#### Container Issues
- **Port Conflicts**: Check for existing services on required ports
- **Network Connectivity**: Verify container networking and DNS resolution
- **Resource Limits**: Monitor container CPU and memory usage

#### Database Connection
- **Connection Strings**: Verify database host and port configuration
- **Credentials**: Ensure correct username and password
- **Network Access**: Check firewall rules and container networking

### Debug Commands

#### Container Logs
```bash
# View real-time logs
docker logs -f silent-api

# View last 100 lines
docker logs --tail 100 silent-db

# View logs with timestamps
docker logs -t silent-app
```

#### Container Resource Usage
```bash
# Live resource monitoring
docker stats

# Specific container stats
docker stats silent-api silent-app silent-db
```

#### Network Debugging
```bash
# List Docker networks
docker network ls

# Inspect network configuration
docker network inspect bridge

# Test connectivity between containers
docker exec silent-api ping silent-db
```

## Future Enhancements

### Planned Improvements
- **Automated Testing**: Integration with test frameworks
- **Monitoring**: Application performance monitoring setup
- **Logging**: Centralized logging with ELK stack
- **Backup Automation**: Automated database backup procedures
- **Load Testing**: Performance testing tools and procedures

### Infrastructure as Code
- **Terraform**: Infrastructure provisioning
- **Ansible**: Configuration management
- **Kubernetes**: Container orchestration for production
- **Helm Charts**: Kubernetes application packaging

## Best Practices

### Development Workflow
1. **Version Control**: Tag releases with semantic versioning
2. **Environment Parity**: Keep development and production environments similar
3. **Documentation**: Keep documentation updated with code changes
4. **Testing**: Test builds locally before pushing to CI/CD
5. **Security**: Regular security updates and dependency scanning

### Deployment Strategy
1. **Blue-Green Deployment**: Zero-downtime deployments
2. **Health Checks**: Verify application health before traffic routing
3. **Rollback Procedures**: Quick rollback capability for failed deployments
4. **Monitoring**: Comprehensive monitoring and alerting
5. **Backup Strategy**: Regular backups with tested restore procedures