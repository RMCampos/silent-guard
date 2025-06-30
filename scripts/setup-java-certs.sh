#!/bin/bash

openssl pkcs12 -export \
  -in certs/silentguard-local.crt \
  -inkey certs/silentguard-local.key \
  -out certs/keystore.p12 \
  -name "silentguard-local" \
  -passout pass:changeit
