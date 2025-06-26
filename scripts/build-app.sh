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

docker build --no-cache \
 --build-arg VITE_BUILD="v999-$(date '+%Y-%m-%d-%H%M%S')" \
 --build-arg SOURCE_PR="v999-123456789-$(date '+%Y-%m-%d-%H%M%S')" \
 -t silent-app:candidate \
 ./silent-app

echo "Done"
