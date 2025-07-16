# Silent Guard API Documentation

## Overview

Silent Guard API is a Java Spring Boot application that implements a "dead man's switch" system. It monitors user activity and automatically sends messages when users fail to check in within specified timeframes. The system is built with Spring Boot 3.5.3 and Java 21, featuring OAuth2 authentication, scheduled email reminders, and PostgreSQL database.

## Technologies & Dependencies

### Core Framework
- **Spring Boot**: 3.5.3
- **Java**: 21
- **Maven**: Build tool
- **Hibernate**: 6.6.19.Final

### Security
- **Spring Security**: OAuth2 Resource Server
- **Auth0**: Authentication provider
- **JJWT**: 0.12.6 (JWT token handling)

### Database
- **PostgreSQL**: Primary database
- **Flyway**: Database migrations
- **Spring Data JPA**: Data access layer

### Email & Caching
- **Mailgun**: Email service provider
- **Caffeine**: In-memory caching

### Utilities
- **Lombok**: Code generation
- **Spring Boot Actuator**: Health monitoring

## Project Structure

```
silent-api/
├── src/
│   ├── main/
│   │   ├── java/br/dev/ricardocampos/silentguardapi/
│   │   │   ├── auth/           # Authentication utilities
│   │   │   ├── config/         # Configuration classes
│   │   │   ├── controller/     # REST controllers
│   │   │   ├── dto/            # Data transfer objects
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── exception/      # Custom exceptions
│   │   │   ├── filter/         # HTTP filters
│   │   │   ├── repository/     # Data repositories
│   │   │   ├── service/        # Business logic
│   │   │   ├── template/       # Email templates
│   │   │   └── util/           # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── db/migration/   # Flyway migrations
│   └── test/                   # Test classes
├── pom.xml
└── Dockerfile
```

## REST API Endpoints

### Authentication Required Endpoints

#### Message Management (`/api/messages`)
- **GET** `/api/messages`
  - **Description**: Retrieve all messages for authenticated user
  - **Authentication**: Required (JWT)
  - **Response**: `200 OK` with `MessageDto[]`

- **PUT** `/api/messages`
  - **Description**: Create a new message
  - **Authentication**: Required (JWT)
  - **Request Body**: `MessageDto`
  - **Response**: `200 OK` with `MessageDto`

- **POST** `/api/messages/{id}`
  - **Description**: Update existing message
  - **Authentication**: Required (JWT)
  - **Path Parameter**: `id` (Long) - Message ID
  - **Request Body**: `MessageDto`
  - **Response**: `200 OK` with `MessageDto`

- **DELETE** `/api/messages/{id}`
  - **Description**: Delete message
  - **Authentication**: Required (JWT)
  - **Path Parameter**: `id` (Long) - Message ID
  - **Response**: `204 No Content`

- **POST** `/api/messages/user`
  - **Description**: Handle user sign-up or sign-in
  - **Authentication**: Required (JWT)
  - **Response**: `204 No Content`

#### Public Endpoints

#### Confirmation (`/api/confirmation`)
- **PUT** `/api/confirmation/check-in/{confirmation}`
  - **Description**: Register user check-in
  - **Authentication**: Not required
  - **Path Parameter**: `confirmation` (String) - Confirmation UUID
  - **Response**: `204 No Content`

### Response Formats

#### Success Responses
- **200 OK**: Successful operations with data
- **204 No Content**: Successful operations without response body

#### Error Responses
- **400 Bad Request**: Validation errors (`ValidationExceptionDto`)
- **401 Unauthorized**: Missing or invalid authentication
- **404 Not Found**: Message not found (`MessageNotFoundException`)
- **503 Service Unavailable**: Mail service errors (`MailServiceException`)

## Data Transfer Objects (DTOs)

### MessageDto
```java
{
  "id": Long,
  "spanDays": Integer,
  "subject": String,
  "targets": String[],
  "content": String,
  "lastReminderSent": LocalDateTime,
  "nextReminderDue": LocalDateTime,
  "lastCheckIn": LocalDateTime,
  "reminderUuid": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### UserInfoDto
```java
{
  "email": String,
  "name": String,
  "nickname": String,
  "picture": String
}
```

### ValidationExceptionDto
```java
{
  "message": String,
  "fields": FieldIssueDto[]
}
```

## Database Schema

### Users Table (`sg_users`)
```sql
CREATE TABLE sg_users (
  id            SERIAL PRIMARY KEY,
  email         VARCHAR(50) NOT NULL UNIQUE,
  last_check_in TIMESTAMP NOT NULL,
  break_days    INTEGER DEFAULT NULL,
  created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMP DEFAULT NULL,
  disabled_at   TIMESTAMP DEFAULT NULL
);
```

### Messages Table (`sg_messages`)
```sql
CREATE TABLE sg_messages (
  id                 SERIAL PRIMARY KEY,
  user_id           INTEGER NOT NULL REFERENCES sg_users(id),
  span_days         INTEGER NOT NULL,
  subject           VARCHAR(300) NOT NULL,
  targets           VARCHAR(3000) NOT NULL,
  content           TEXT NOT NULL,
  last_reminder_sent TIMESTAMP DEFAULT NULL,
  next_reminder_due  TIMESTAMP DEFAULT NULL,
  last_check_in     TIMESTAMP DEFAULT NULL,
  reminder_uuid     UUID UNIQUE NOT NULL,
  created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMP DEFAULT NULL,
  disabled_at       TIMESTAMP DEFAULT NULL
);
```

## Service Layer Architecture

### UserService
- **Purpose**: User management and authentication
- **Key Methods**:
  - `signUpOrSignUser()`: Handle user registration/login
- **Features**: JWT validation, automatic user creation, check-in updates

### MessageService
- **Purpose**: Message CRUD operations and check-in processing
- **Key Methods**:
  - `getMessages()`: Retrieve user messages
  - `createMessage(MessageDto)`: Create new message
  - `updateMessage(Long, MessageDto)`: Update existing message
  - `deleteMessage(Long)`: Delete message
  - `registerUserCheckIn(String)`: Process user check-ins
- **Features**: Transactional operations, reminder scheduling

### AuthService
- **Purpose**: Auth0 integration and user info retrieval
- **Key Methods**:
  - `getUserInfo(String token)`: Fetch user info from Auth0
- **Features**: Cached user info, token validation

### PersistentReminderService
- **Purpose**: Scheduled task management for email reminders
- **Key Methods**:
  - `scheduleCheckingMessage(MessageEntity)`: Schedule reminder emails
  - `scheduleContentMessage(MessageEntity)`: Schedule content delivery
  - `cancelExistingTask(Long, boolean)`: Cancel scheduled tasks
  - `restoreSchedulesOnStartup()`: Restore schedules on app startup
- **Features**: Dual-phase reminder system, persistent scheduling

### MailgunEmailService
- **Purpose**: Email delivery via Mailgun API
- **Key Methods**:
  - `sendCheckInRequest(List<String>, String)`: Send check-in reminders
  - `sendHtmlContentMessage(List<String>, String, String)`: Send HTML content
- **Features**: Template support, carbon copy handling

## Configuration

### Security Configuration (`SecurityConfig`)
- **JWT Authentication**: OAuth2 resource server configuration
- **CORS Support**: Configurable allowed origins
- **Endpoint Security**:
  - `/api/confirmation/**`: Public access
  - `/api/messages/**`: Authentication required
  - OPTIONS requests: Permitted

### Application Configuration (`AppConfig`)
- **Auth0 Settings**: Domain, API identifier
- **Mailgun Settings**: API key, domain, sender email
- **Target Environment**: Development/production modes

### Cache Configuration (`CacheConfig`)
- **Caffeine Cache**: In-memory caching
- **TTL**: 180 minutes for user info
- **Max Size**: 2000 entries

## Environment Variables

### Required Variables
```bash
# Auth0 Configuration
AUTH_DOMAIN=https://your-domain.auth0.com
API_IDENTIFIER=your-api-identifier

# Database Configuration
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=sg
POSTGRES_USER=sg
POSTGRES_PASSWORD=your-password

# Mailgun Configuration
MAILGUN_APIKEY=your-mailgun-api-key

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:5173

# Application Settings
TARGET_ENV=development
API_LOGGING_LEVEL=INFO
```

## Email Template System

### Template Types
1. **Check-in Reminders**: Sent at configured intervals
2. **Content Messages**: Sent 12 hours after missed check-in
3. **Message Deactivation**: Automatic after content delivery

### Template Classes
- **MailgunTemplateCheckIn**: User reminder emails
- **MailgunTemplateHtml**: Content delivery emails

## Development Commands

### Local Development
```bash
# Run application
./mvnw spring-boot:run

# Run with debug
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005"

# Run tests
./mvnw test

# Package application
./mvnw clean package

# Build native image
./mvnw -B package -Pnative -DskipTests
```

### Docker Development
```bash
# Build Docker image
docker build -t silent-api:latest .

# Run with environment file
docker run -p 8080:8080 --env-file .env silent-api:latest
```

## Key Features

### Dead Man's Switch Logic
1. **User Check-in**: Users must check in within configured timeframes
2. **Reminder Phase**: Email reminders sent at intervals
3. **Content Delivery**: Final message sent after grace period
4. **Automatic Deactivation**: Messages disabled after delivery

### Security Features
- **JWT Authentication**: Auth0 integration with token validation
- **CORS Protection**: Configurable allowed origins
- **Input Validation**: Comprehensive request validation
- **Rate Limiting**: Built-in through caching

### Monitoring & Health
- **Actuator Endpoints**: Health checks and metrics
- **Build Info**: Version tracking in response headers
- **Logging**: Configurable logging levels

## Error Handling

### Custom Exceptions
- **InvalidUserException**: Authentication/user validation errors
- **MessageNotFoundException**: Message not found errors
- **MailServiceException**: Email service failures

### Global Exception Handler
- **RestExceptionController**: Centralized error handling with detailed field validation

## Testing

### Current Test Structure
- **SilentGuardApiApplicationTests**: Basic Spring Boot context test
- **Integration Tests**: Not currently implemented
- **Unit Tests**: Not currently implemented

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=SilentGuardApiApplicationTests

# Skip tests during build
./mvnw clean package -DskipTests
```

## Deployment

### Docker Deployment
```bash
# Build for production
docker build --no-cache \
  --build-arg BUILD="v1.0.0" \
  --build-arg SOURCE_PR="pr-123" \
  -t silent-api:production .

# Run in production
docker run -d -p 8080:8080 \
  --env-file .env.production \
  silent-api:production
```

### Native Image Deployment
```bash
# Build native image
./mvnw -B package -Pnative -DskipTests

# Run native executable
./target/silentguardapi
```

## API Usage Examples

### Authentication
```bash
# Get access token from Auth0
curl -X POST "https://your-domain.auth0.com/oauth/token" \
  -H "Content-Type: application/json" \
  -d '{
    "client_id": "your-client-id",
    "client_secret": "your-client-secret",
    "audience": "your-api-identifier",
    "grant_type": "client_credentials"
  }'
```

### Message Management
```bash
# Create message
curl -X PUT "http://localhost:8080/api/messages" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "spanDays": 7,
    "subject": "Weekly Check-in",
    "targets": ["recipient@example.com"],
    "content": "This is my weekly check-in message."
  }'

# Get messages
curl -X GET "http://localhost:8080/api/messages" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Register check-in
curl -X PUT "http://localhost:8080/api/confirmation/check-in/YOUR_UUID"
```

## Performance Considerations

### Caching Strategy
- **User Info**: Cached for 180 minutes
- **Authentication**: Cached for 150 minutes
- **Database Queries**: Optimized with proper indexing

### Database Optimization
- **Indexes**: On email, reminder_uuid, user_id
- **Connection Pooling**: HikariCP configuration
- **Query Optimization**: JPA query optimization

## Troubleshooting

### Common Issues
1. **Authentication Errors**: Check Auth0 configuration and token validity
2. **Database Connection**: Verify PostgreSQL connection parameters
3. **Email Delivery**: Check Mailgun API key and domain configuration
4. **CORS Issues**: Verify allowed origins configuration

### Debug Endpoints
- **Health Check**: `/actuator/health`
- **Build Info**: Available in response headers (`X-BUILD-INFO`)
- **Database Status**: Check via health endpoint

## Future Enhancements

### Planned Features
- **Message Templates**: Pre-defined message templates
- **Multiple Reminders**: Configurable reminder schedules
- **User Dashboard**: Web interface for message management
- **API Versioning**: Version-specific endpoints
- **Enhanced Security**: Rate limiting, IP whitelisting
- **Monitoring**: Comprehensive metrics and alerting