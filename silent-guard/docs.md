# Silent Guard API Testing Collection Documentation

## Overview

The `silent-guard/` directory contains a Bruno API testing collection for the Silent Guard application. Bruno is a modern, open-source API client that allows developers to test, document, and collaborate on API development. This collection provides pre-configured requests for testing the Silent Guard API endpoints and external integrations.

## About Bruno

Bruno is a fast and lightweight API client designed as an alternative to Postman. Key features include:
- **File-based collections**: Version control friendly
- **Offline-first**: No cloud dependency required
- **Lightweight**: Fast startup and execution
- **Open source**: Transparent and extensible
- **Git-friendly**: Plain text format for version control

## Collection Structure

### Collection Configuration (bruno.json)
```json
{
  "version": "1",
  "name": "silent-guard",
  "type": "collection",
  "ignore": [
    "node_modules",
    ".git"
  ]
}
```

**Configuration Details**:
- **Version**: Bruno collection format version 1
- **Name**: `silent-guard` - Collection identifier
- **Type**: Standard HTTP collection
- **Ignore**: Files excluded from collection scanning

## API Test Requests

### 1. Test API (Test API.bru)
**Purpose**: Basic API health check and message retrieval test

**Request Details**:
- **Method**: GET
- **URL**: `http://localhost:8080/api/messages`
- **Authentication**: Inherit (uses collection-level auth)
- **Body**: None
- **Sequence**: 3

**Use Cases**:
- Verify API is running and accessible
- Test authentication flow
- Validate message endpoint functionality
- Debug API connectivity issues

**Expected Response**:
```json
[
  {
    "id": 1,
    "spanDays": 7,
    "subject": "Weekly Check-in",
    "targets": ["recipient@example.com"],
    "content": "This is my weekly message.",
    "lastReminderSent": null,
    "nextReminderDue": "2024-01-22T10:00:00",
    "reminderUuid": "uuid-string",
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": null
  }
]
```

### 2. Get User Info (Get User Info.bru)
**Purpose**: Test Auth0 user information retrieval

**Request Details**:
- **Method**: GET
- **URL**: `https://dev-aaa.us.auth0.com/userinfo`
- **Authentication**: Inherit (Bearer token required)
- **Body**: None
- **Sequence**: 2

**Use Cases**:
- Validate Auth0 token functionality
- Test user authentication flow
- Debug Auth0 integration issues
- Verify user profile data

**Expected Response**:
```json
{
  "sub": "auth0|user-id",
  "name": "User Name",
  "nickname": "username",
  "email": "user@example.com",
  "email_verified": true,
  "picture": "https://s.gravatar.com/avatar/...",
  "updated_at": "2024-01-15T10:00:00.000Z"
}
```

### 3. Mailgun (Mailgun.bru)
**Purpose**: Test Mailgun email service integration

**Request Details**:
- **Method**: POST
- **URL**: `https://api.mailgun.net/v3/ricardocampos.dev.br/messages`
- **Authentication**: Inherit (API key authentication required)
- **Body**: None (requires form data for actual sends)
- **Sequence**: 4

**Use Cases**:
- Test email service connectivity
- Validate Mailgun API integration
- Debug email delivery issues
- Verify domain configuration

**Authentication Requirements**:
- **Type**: Basic Auth
- **Username**: `api`
- **Password**: Mailgun API key

**Example Request Body** (for actual email sending):
```
to: recipient@example.com
from: Silent Guard <no-reply@ricardocampos.dev.br>
subject: Test Email
text: This is a test email from Silent Guard
```

## Authentication Setup

### Collection-Level Authentication
Bruno supports inheritance of authentication settings across requests. Set up authentication at the collection level:

#### For API Requests (Silent Guard API)
1. **Type**: Bearer Token
2. **Token**: JWT token from Auth0
3. **Prefix**: `Bearer`

#### For Auth0 Requests
1. **Type**: Bearer Token
2. **Token**: Access token from Auth0 login

#### For Mailgun Requests
1. **Type**: Basic Auth
2. **Username**: `api`
3. **Password**: Your Mailgun API key

### Environment Variables
Bruno supports environment variables for dynamic configuration:

```javascript
// Environment configuration
{
  "api_base_url": "http://localhost:8080",
  "auth0_domain": "https://dev-aaa.us.auth0.com",
  "mailgun_domain": "https://api.mailgun.net/v3/ricardocampos.dev.br",
  "access_token": "your-jwt-token",
  "mailgun_api_key": "your-mailgun-key"
}
```

## Testing Workflows

### Complete API Testing Flow
1. **Get Auth0 Token**: Use Auth0 login to obtain access token
2. **Get User Info**: Validate token with Auth0 userinfo endpoint
3. **Test API**: Call Silent Guard API with authenticated token
4. **Test Mailgun**: Verify email service integration

### Development Testing
```bash
# Start local development environment
docker-compose up -d

# Wait for services to be ready
sleep 30

# Open Bruno and run test collection
bruno run silent-guard/
```

### Automated Testing Integration
Bruno can be integrated into CI/CD pipelines:

```bash
# Install Bruno CLI
npm install -g @usebruno/cli

# Run collection
bruno run silent-guard/ --env local

# Run specific request
bruno run silent-guard/Test\ API.bru --env local
```

## Request Customization

### Adding Headers
```javascript
// In .bru file
headers {
  Content-Type: application/json
  X-Custom-Header: custom-value
}
```

### Request Body Examples

#### Create Message Request
```javascript
// POST /api/messages
body {
  {
    "spanDays": 7,
    "subject": "Weekly Check-in",
    "targets": ["recipient@example.com"],
    "content": "This is my weekly check-in message."
  }
}
```

#### Update Message Request
```javascript
// POST /api/messages/1
body {
  {
    "id": 1,
    "spanDays": 14,
    "subject": "Bi-weekly Check-in",
    "targets": ["recipient@example.com"],
    "content": "Updated message content."
  }
}
```

### Query Parameters
```javascript
// In .bru file
query {
  limit: 10
  offset: 0
  status: active
}
```

## Environment Management

### Multiple Environments
Bruno supports multiple environments for different deployment stages:

#### Local Development
```json
{
  "name": "local",
  "variables": {
    "base_url": "http://localhost:8080",
    "auth0_domain": "https://dev-aaa.us.auth0.com"
  }
}
```

#### Staging Environment
```json
{
  "name": "staging",
  "variables": {
    "base_url": "https://api-staging.silentguard.com",
    "auth0_domain": "https://staging-auth.auth0.com"
  }
}
```

#### Production Environment
```json
{
  "name": "production",
  "variables": {
    "base_url": "https://api.silentguard.com",
    "auth0_domain": "https://prod-auth.auth0.com"
  }
}
```

## Advanced Testing Features

### Pre-request Scripts
```javascript
// Pre-request script example
const token = await getAuthToken();
bru.setVar("access_token", token);
```

### Post-response Scripts
```javascript
// Post-response script example
if (res.status === 200) {
  const data = res.body;
  bru.setVar("message_id", data.id);
}
```

### Assertions
```javascript
// Response assertions
expect(res.status).to.equal(200);
expect(res.body).to.have.property('id');
expect(res.headers['content-type']).to.contain('application/json');
```

## Debugging and Troubleshooting

### Common Issues

#### Authentication Failures
- **401 Unauthorized**: Check if JWT token is valid and not expired
- **403 Forbidden**: Verify token has required scopes
- **Invalid Token**: Ensure Bearer prefix is included

#### Connection Issues
- **Connection Refused**: Verify API server is running
- **DNS Resolution**: Check if hostname/domain is accessible
- **CORS Errors**: Verify CORS configuration on server

#### Request Failures
- **400 Bad Request**: Check request body format and required fields
- **404 Not Found**: Verify endpoint URL and HTTP method
- **500 Internal Server Error**: Check server logs for detailed error

### Debug Techniques

#### Enable Request Logging
```javascript
// Log request details
console.log('Request URL:', req.url);
console.log('Request Headers:', req.headers);
console.log('Request Body:', req.body);
```

#### Response Inspection
```javascript
// Log response details
console.log('Response Status:', res.status);
console.log('Response Headers:', res.headers);
console.log('Response Body:', res.body);
```

#### Network Analysis
- Use browser dev tools to inspect network requests
- Check for proper SSL certificate validation
- Verify request/response headers and timing

## Integration with Development Workflow

### Version Control
Bruno collections are git-friendly:
```bash
# Add collection to git
git add silent-guard/

# Commit changes
git commit -m "Add API testing collection"

# Share with team
git push origin main
```

### Documentation
Bruno requests serve as living documentation:
- **API Contract**: Request/response examples
- **Authentication**: Token requirements and scopes
- **Error Handling**: Expected error responses
- **Integration**: External service dependencies

### Collaborative Testing
- **Shared Collections**: Team members can share and update tests
- **Environment Configs**: Different configs for different developers
- **Test Results**: Shareable test execution results

## Security Considerations

### Sensitive Data
- **API Keys**: Store in environment variables, not in collection files
- **Tokens**: Use short-lived tokens and refresh regularly
- **Personal Data**: Avoid committing real user data in test requests

### Best Practices
- **Environment Separation**: Use different API keys for different environments
- **Token Rotation**: Regularly rotate API keys and access tokens
- **Access Control**: Limit API key permissions to minimum required scope

## Performance Testing

### Load Testing Integration
Bruno can be integrated with load testing tools:

```bash
# Export Bruno requests for load testing
bruno export silent-guard/ --format newman

# Use with Apache Bench
ab -n 1000 -c 10 http://localhost:8080/api/messages
```

### Response Time Monitoring
```javascript
// Measure response time
const startTime = Date.now();
// ... make request
const responseTime = Date.now() - startTime;
console.log(`Response time: ${responseTime}ms`);
```

## Future Enhancements

### Planned Additions
- **Additional Endpoints**: More comprehensive API coverage
- **Error Scenarios**: Test error handling and edge cases
- **Data Validation**: Schema validation for responses
- **Performance Tests**: Response time benchmarks
- **Mock Responses**: Mock data for offline development

### Integration Possibilities
- **CI/CD Pipeline**: Automated testing in build process
- **Monitoring**: Health check automation
- **Documentation**: Auto-generated API docs from tests
- **Contract Testing**: API contract validation