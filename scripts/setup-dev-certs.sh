#!/bin/bash
# Run from inside the project root dir
if [ ! -f "certs/localhost.key" ]; then
  mkdir -p certs
  openssl req -x509 -newkey rsa:4096 \
    -keyout certs/silentguard-local.key \
    -out certs/silentguard-local.crt \
    -days 365 -nodes \
    -subj "/CN=silentguard-local.ricardocampos.dev.br"
fi
