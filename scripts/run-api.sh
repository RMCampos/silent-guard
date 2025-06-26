#!/bin/bash

if [ ! -f "silent-api/.env" ]; then
  echo "Env file not found. Creating..."
  echo "Creating basic env file"
  cd server

  echo "API_IDENTIFIER=http://localhost:8080" > .env
  echo "AUTH_DOMAIN=https://dev-aaa.us.auth0.com" >> .env
  echo "CORS_ALLOWED_ORIGINS=http://localhost:5173" >> .env
  echo "MAILGUN_APIKEY=abcdef123456789abcdef" >> .env
  echo "API_LOGGING_LEVEL=INFO" >> .env
  echo "POSTGRES_DB=sg" >> .env
  echo "POSTGRES_USER=sg" >> .env
  echo "POSTGRES_PASSWORD=default" >> .env
  echo "POSTGRES_PORT=5432" >> .env
  echo "POSTGRES_HOST=localhost" >> .env
  echo "TARGET_ENV=development" >> .env
else
  echo "Env file in place, nothing to do."
fi

source silent-api/.env

DOCKER_HOST=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' silent-db)
echo "Docker DB host: $DOCKER_HOST"

echo "Starting silent-api..."

docker run -d -p 8080:8080 --rm \
  --name silent-api \
  -e AUTH_DOMAIN=$AUTH_DOMAIN \
  -e API_IDENTIFIER=$API_IDENTIFIER \
  -e POSTGRES_DB=$POSTGRES_DB \
  -e POSTGRES_USER=$POSTGRES_USER \
  -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
  -e POSTGRES_PORT=$POSTGRES_PORT \
  -e POSTGRES_HOST=$DOCKER_HOST \
  -e CORS_ALLOWED_ORIGINS=$CORS_ALLOWED_ORIGINS \
  -e MAILGUN_APIKEY=$MAILGUN_APIKEY \
  -e TARGET_ENV=$TARGET_ENV \
  -e API_LOGGING_LEVEL=$API_LOGGING_LEVEL \
  silent-api:candidate
