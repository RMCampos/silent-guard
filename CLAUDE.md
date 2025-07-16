# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Silent Guard is a dead man's switch application with a Spring Boot REST API backend and React frontend. The system uses Auth0 for authentication and Mailgun for email services.

## Architecture

### Backend (silent-api/)
- **Framework**: Spring Boot 3.5.3 with Java 21
- **Database**: PostgreSQL with Flyway migrations
- **Authentication**: Auth0 JWT tokens with Spring Security
- **Email Service**: Mailgun for notifications
- **Caching**: Caffeine cache implementation
- **Build Tool**: Maven with GraalVM native image support

### Frontend (silent-app/)
- **Framework**: React 19.1.0 with TypeScript
- **Build Tool**: Vite 7.0.0
- **Styling**: Tailwind CSS 4.1.11
- **Authentication**: Auth0 React SDK
- **Rich Text**: Lexical editor framework

### Key Components
- **JWT Authentication**: Custom filter in `JwtAuthenticationFilter.java`
- **Message System**: Core entities in `MessageEntity.java` and `UserEntity.java`
- **Email Templates**: Mailgun templates in `template/` package
- **API Controllers**: REST endpoints in `controller/` package
- **Services**: Business logic in `service/` package with persistent reminder system

## Development Commands

### Frontend (silent-app/)
```bash
# Install dependencies
npm ci

# Development server (with HTTPS)
npm run dev

# Production build
npm run build

# Lint code
npm run lint

# Preview production build
npm run preview
```

### Backend (silent-api/)
```bash
# Run development server
./mvnw spring-boot:run

# Run with debug mode
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005"

# Build JAR
./mvnw clean package

# Build native image
./mvnw -B package -Pnative -DskipTests

# Run tests (currently minimal)
./mvnw test

# Build fat JAR
./mvnw package spring-boot:repackage -DskipTests
```

### Docker Development
```bash
# Start all services
docker-compose up -d

# Start database only
./scripts/run-db.sh

# Start API only
./scripts/run-api.sh

# Start app only
./scripts/run-app.sh

# Build API image
./scripts/build-api.sh

# Build app image
./scripts/build-app.sh
```

## Environment Setup

### API Environment (.env in silent-api/)
Required variables:
- `AUTH_DOMAIN`: Auth0 domain
- `API_IDENTIFIER`: Auth0 API identifier
- `POSTGRES_*`: Database connection details
- `MAILGUN_APIKEY`: Mailgun API key
- `CORS_ALLOWED_ORIGINS`: Frontend URLs

### Frontend Environment
- Uses `auth_config.json` for Auth0 configuration
- Vite configuration supports HTTPS in development with local certificates

## Security Configuration

### HTTPS Development
- Local certificates in `certs/` directory
- Setup scripts: `setup-dev-certs.sh` and `setup-java-certs.sh`
- Vite configured for HTTPS with `silentguard-local.ricardocampos.dev.br`

### Authentication Flow
- Auth0 JWT tokens validated in `JwtAuthenticationFilter`
- Bearer token extraction in `BearerTokenHolder`
- Security configuration in `SecurityConfig.java`

## Database

### Flyway Migrations
- Migration scripts in `silent-api/src/main/resources/db/migration/`
- Base schema: `V1__create_base_schema.sql`

### Entities
- `UserEntity`: User management with Auth0 integration
- `MessageEntity`: Core message system for dead man's switch

## Testing

### Current State
- **Frontend**: No testing framework configured
- **Backend**: Basic Spring Boot test (`SilentGuardApiApplicationTests`)
- **CI/CD**: Tests are currently skipped with `-DskipTests`
- **API Testing**: Bruno collection in `silent-guard/` directory

### Test Commands
```bash
# Backend tests (minimal)
./mvnw test

# Frontend linting (no unit tests)
npm run lint
```

## API Testing

### Bruno Collection
Located in `silent-guard/` directory:
- `Test API.bru`: Basic API health check
- `Get User Info.bru`: User info endpoint
- `Mailgun.bru`: Email service testing

## Build and Deployment

### Native Image Support
- GraalVM configuration in `pom.xml`
- Native hints in `src/main/resources/META-INF/native-image/`
- Build command: `./mvnw -B package -Pnative -DskipTests`

### Docker Configuration
- API: Port 8443 (HTTPS)
- App: Port 5173 (HTTPS)
- Database: Port 5432
- Debug port: 5005

## Key Files to Understand

### Backend Configuration
- `SecurityConfig.java`: Security and CORS setup
- `JwtAuthenticationFilter.java`: JWT authentication logic
- `MessageService.java`: Core business logic
- `PersistentReminderService.java`: Reminder system

### Frontend Architecture
- `TokenContext.tsx`: Auth token management
- `apiService.ts`: API communication layer
- `RichTextEditor.tsx`: Lexical-based rich text editor
- `DashboardPage.tsx`: Main application interface

## Development Notes

- The application uses HTTPS in development with local certificates
- Database migrations are handled by Flyway automatically
- Authentication is centralized through Auth0
- The system is designed for cloud-native deployment with GraalVM
- Email notifications are sent through Mailgun templates
- The frontend uses Tailwind CSS with a custom design system