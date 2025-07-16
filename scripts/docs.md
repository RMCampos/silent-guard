# Scripts Documentation

## Overview

The `scripts/` directory contains shell scripts for building, running, and setting up the Silent Guard application components. These scripts provide convenient automation for development, testing, and deployment tasks across the different services (API, frontend app, and database).

## Available Scripts

### Build Scripts

#### build-api.sh
**Purpose**: Build Docker image for the Silent Guard API (Spring Boot backend)

**Features**:
- Creates default `.env` file if missing with development configuration
- Sources environment variables from `silent-api/.env`
- Builds Docker image with timestamped build arguments
- Uses `--no-cache` to ensure fresh builds

**Usage**:
```bash
./scripts/build-api.sh
```

**Environment Variables Created**:
```bash
API_IDENTIFIER=http://localhost:8080
AUTH_DOMAIN=https://dev-aaa.us.auth0.com
CORS_ALLOWED_ORIGINS=http://localhost:5173
MAILGUN_APIKEY=abcdef123456789abcdef
API_LOGGING_LEVEL=INFO
POSTGRES_DB=sg
POSTGRES_USER=sg
POSTGRES_PASSWORD=default
POSTGRES_PORT=5432
POSTGRES_HOST=localhost
TARGET_ENV=development
```

**Docker Build Details**:
- **Image Tag**: `silent-api:candidate`
- **Build Args**: 
  - `BUILD`: Version with timestamp (e.g., `v999-2024-01-15-143022`)
  - `SOURCE_PR`: PR identifier with timestamp
- **Context**: `./silent-api` directory

#### build-app.sh
**Purpose**: Build Docker image for the Silent Guard React frontend application

**Features**:
- Creates default `.env` file if missing for frontend configuration
- Sources environment variables from `silent-app/.env`
- Builds Docker image with build version and PR tracking
- Uses `--no-cache` for fresh builds

**Usage**:
```bash
./scripts/build-app.sh
```

**Environment Variables Created**:
```bash
VITE_BACKEND_API=http://localhost:8080
```

**Docker Build Details**:
- **Image Tag**: `silent-app:candidate`
- **Build Args**:
  - `VITE_BUILD`: Version with timestamp (e.g., `v999-2024-01-15-143022`)
  - `SOURCE_PR`: PR identifier with timestamp
- **Context**: `./silent-app` directory

### Runtime Scripts

#### run-db.sh
**Purpose**: Start PostgreSQL database container for development

**Features**:
- Creates default `.env` file if missing with database configuration
- Sources environment variables from `silent-api/.env`
- Runs PostgreSQL 15.8 in Docker container
- Exposes database on port 5432

**Usage**:
```bash
./scripts/run-db.sh
```

**Container Details**:
- **Image**: `postgres:15.8-bookworm`
- **Container Name**: `silent-db`
- **Port Mapping**: `5432:5432`
- **Flags**: `-d` (detached), `--rm` (auto-remove)

#### run-api.sh
**Purpose**: Start Silent Guard API container for development

**Features**:
- Creates default `.env` file if missing
- Sources environment variables from `silent-api/.env`
- Detects Docker database host dynamically
- Runs API container with all required environment variables

**Usage**:
```bash
./scripts/run-api.sh
```

**Container Details**:
- **Image**: `silent-api:candidate`
- **Container Name**: `silent-api`
- **Port Mapping**: `8080:8080`
- **Dynamic DB Host**: Automatically detects `silent-db` container IP

**Environment Variables Passed**:
- Auth0 configuration (domain, identifier)
- Database connection details
- CORS settings
- Mailgun API configuration
- Logging and environment settings

#### run-app.sh
**Purpose**: Start Silent Guard React frontend container for development

**Features**:
- Creates default `.env` file if missing for frontend
- Sources environment variables from `silent-app/.env`
- Runs frontend container with backend API configuration

**Usage**:
```bash
./scripts/run-app.sh
```

**Container Details**:
- **Image**: `silent-app:candidate`
- **Container Name**: `silent-app`
- **Port Mapping**: `5173:5173`
- **Environment**: Backend API URL configuration

### Certificate Setup Scripts

#### setup-dev-certs.sh
**Purpose**: Generate SSL certificates for local HTTPS development

**Features**:
- Creates `certs/` directory if it doesn't exist
- Generates self-signed SSL certificate for local development
- Uses RSA 4096-bit encryption
- Valid for 365 days
- Configured for specific development domain

**Usage**:
```bash
./scripts/setup-dev-certs.sh
```

**Certificate Details**:
- **Key File**: `certs/silentguard-local.key`
- **Certificate File**: `certs/silentguard-local.crt`
- **Common Name**: `silentguard-local.ricardocampos.dev.br`
- **Key Size**: RSA 4096-bit
- **Validity**: 365 days
- **No Password**: `-nodes` flag for development convenience

**OpenSSL Command**:
```bash
openssl req -x509 -newkey rsa:4096 \
  -keyout certs/silentguard-local.key \
  -out certs/silentguard-local.crt \
  -days 365 -nodes \
  -subj "/CN=silentguard-local.ricardocampos.dev.br"
```

#### setup-java-certs.sh
**Purpose**: Convert SSL certificates to Java keystore format for Spring Boot HTTPS

**Features**:
- Converts PEM certificates to PKCS12 format
- Creates Java-compatible keystore
- Uses standard Java keystore password
- Enables HTTPS for Spring Boot development

**Usage**:
```bash
./scripts/setup-java-certs.sh
```

**Output**:
- **Keystore File**: `certs/keystore.p12`
- **Keystore Type**: PKCS12
- **Alias**: `silentguard-local`
- **Password**: `changeit` (standard Java development password)

**OpenSSL Command**:
```bash
openssl pkcs12 -export \
  -in certs/silentguard-local.crt \
  -inkey certs/silentguard-local.key \
  -out certs/keystore.p12 \
  -name "silentguard-local" \
  -passout pass:changeit
```

## Development Workflow

### Initial Setup
```bash
# 1. Generate SSL certificates for HTTPS development
./scripts/setup-dev-certs.sh
./scripts/setup-java-certs.sh

# 2. Start database
./scripts/run-db.sh

# 3. Build and start API
./scripts/build-api.sh
./scripts/run-api.sh

# 4. Build and start frontend
./scripts/build-app.sh
./scripts/run-app.sh
```

### Complete Development Stack
```bash
# Start all services in order
./scripts/run-db.sh && \
./scripts/build-api.sh && \
./scripts/run-api.sh && \
./scripts/build-app.sh && \
./scripts/run-app.sh
```

### Using Docker Compose (Alternative)
Instead of individual scripts, you can use:
```bash
docker-compose up -d
```

## Environment Configuration

### Default Development Configuration

#### API Environment (silent-api/.env)
```bash
API_IDENTIFIER=http://localhost:8080
AUTH_DOMAIN=https://dev-aaa.us.auth0.com
CORS_ALLOWED_ORIGINS=http://localhost:5173
MAILGUN_APIKEY=abcdef123456789abcdef
API_LOGGING_LEVEL=INFO
POSTGRES_DB=sg
POSTGRES_USER=sg
POSTGRES_PASSWORD=default
POSTGRES_PORT=5432
POSTGRES_HOST=localhost
TARGET_ENV=development
```

#### Frontend Environment (silent-app/.env)
```bash
VITE_BACKEND_API=http://localhost:8080
```

### Production Configuration
For production use, update the environment files with:
- Real Auth0 domain and API identifier
- Production Mailgun API key
- Secure database credentials
- Production CORS origins
- HTTPS URLs where applicable

## SSL Certificate Management

### Development HTTPS Setup
1. **Generate Certificates**: Run `setup-dev-certs.sh`
2. **Create Java Keystore**: Run `setup-java-certs.sh`
3. **Configure DNS**: Add `silentguard-local.ricardocampos.dev.br` to `/etc/hosts`
4. **Trust Certificate**: Add certificate to browser/system trust store

### Certificate Files
- **Private Key**: `certs/silentguard-local.key` (Never commit to version control)
- **Certificate**: `certs/silentguard-local.crt` (Public certificate)
- **Java Keystore**: `certs/keystore.p12` (For Spring Boot HTTPS)

### Security Notes
- Certificates are self-signed for development only
- Private keys should never be committed to version control
- Use proper CA-signed certificates for production
- The default keystore password (`changeit`) should be changed for production

## Docker Container Management

### Container Lifecycle
```bash
# Start containers
./scripts/run-db.sh
./scripts/run-api.sh
./scripts/run-app.sh

# Check running containers
docker ps

# View logs
docker logs silent-db
docker logs silent-api
docker logs silent-app

# Stop containers
docker stop silent-db silent-api silent-app

# Remove containers (automatically done with --rm flag)
docker rm silent-db silent-api silent-app
```

### Container Networking
- **Database**: Accessible at `localhost:5432`
- **API**: Accessible at `localhost:8080` 
- **Frontend**: Accessible at `localhost:5173`
- **Inter-container**: API automatically detects database container IP

## Troubleshooting

### Common Issues

#### Certificate Problems
```bash
# Regenerate certificates if expired or invalid
rm certs/*
./scripts/setup-dev-certs.sh
./scripts/setup-java-certs.sh
```

#### Container Port Conflicts
```bash
# Check if ports are in use
netstat -tlnp | grep :5432
netstat -tlnp | grep :8080
netstat -tlnp | grep :5173

# Stop conflicting containers
docker stop $(docker ps -q)
```

#### Database Connection Issues
```bash
# Check database container status
docker logs silent-db

# Verify database is accessible
docker exec -it silent-db psql -U sg -d sg -c "SELECT 1;"
```

#### Environment File Issues
```bash
# Remove and regenerate environment files
rm silent-api/.env silent-app/.env
./scripts/build-api.sh  # Will recreate .env
./scripts/build-app.sh  # Will recreate .env
```

### Debug Commands
```bash
# Check container networking
docker inspect silent-db | grep IPAddress
docker inspect silent-api | grep IPAddress

# View environment variables
docker exec silent-api env | grep POSTGRES
docker exec silent-app env | grep VITE

# Test API connectivity
curl -k https://localhost:8080/actuator/health
curl -k https://localhost:5173
```

## Script Customization

### Modifying Build Arguments
Edit the build scripts to customize Docker build arguments:
```bash
# In build-api.sh or build-app.sh
--build-arg BUILD="custom-version" \
--build-arg SOURCE_PR="custom-pr-id" \
```

### Custom Environment Variables
Add additional environment variables to the scripts:
```bash
# In run-api.sh
-e CUSTOM_VARIABLE=$CUSTOM_VARIABLE \
```

### Different Image Tags
Change image tags for different environments:
```bash
# Development
-t silent-api:dev

# Staging
-t silent-api:staging

# Production
-t silent-api:latest
```

## Best Practices

### Development Workflow
1. Always start with database container first
2. Build images before running containers
3. Check logs if containers fail to start
4. Use `docker-compose` for coordinated startup
5. Clean up containers regularly

### Security Considerations
- Never commit `.env` files with real credentials
- Use different certificates for production
- Rotate API keys regularly
- Use secure passwords for production databases
- Enable container security scanning

### Performance Optimization
- Use Docker layer caching for faster builds
- Clean up unused images regularly: `docker image prune`
- Monitor container resource usage: `docker stats`
- Use multi-stage Docker builds for smaller images

## Integration with CI/CD

### GitHub Actions Integration
The scripts can be integrated with CI/CD pipelines:
```yaml
- name: Build API
  run: ./scripts/build-api.sh

- name: Build App  
  run: ./scripts/build-app.sh

- name: Run Tests
  run: |
    ./scripts/run-db.sh
    sleep 10  # Wait for database
    ./scripts/run-api.sh
    # Run tests here
```

### Production Deployment
For production, modify scripts to:
- Use production environment variables
- Push images to container registry
- Use proper SSL certificates
- Enable health checks and monitoring