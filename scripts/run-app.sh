#!/bin/bash

if [ ! -f "silent-app/.env" ]; then
  echo "Env file not found. Creating..."
  echo "Creating basic env file"
  cd server

  echo "VITE_BACKEND_API=http://localhost:8080" > .env
else
  echo "Env file in place, nothing to do."
fi

source silent-app/.env

echo "Starting silent-api..."

docker run -d -p 5173:5173 --rm \
  --name silent-app \
  -e VITE_BACKEND_API=$VITE_BACKEND_API \
  silent-app:candidate
