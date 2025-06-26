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

docker build --no-cache \
 --build-arg BUILD="v999-$(date '+%Y-%m-%d-%H%M%S')" \
 --build-arg SOURCE_PR="v999-123456789-$(date '+%Y-%m-%d-%H%M%S')" \
 -t silent-api:candidate \
 ./silent-api

echo "Done"
