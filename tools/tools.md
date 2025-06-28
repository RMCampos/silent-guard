# Tools

All kind of tools and useful links and commands can be found here!

## Links

- **GitHub Container Registry:** https://ghcr.io/
- **Time tracking:** https://track.toggl.com/timer

## Building locally

**Frontend Web App:**

If you want to build with Docker:

```bash
docker build --no-cache \
 --build-arg VITE_BUILD="v999-$(date '+%Y-%m-%d-%H%M%S')" \
 --build-arg SOURCE_PR="v999-123456789-$(date '+%Y-%m-%d-%H%M%S')" \
 -t client:candidate \
 ./silent-app
```

That's it!

**Backend REST API:**

For the backend there's a Dockerfile ready, just run (from the project root):

```bash
docker build --no-cache \
 --build-arg BUILD="v999-$(date '+%Y-%m-%d-%H%M%S')" \
 --build-arg SOURCE_PR="v999-123456789-$(date '+%Y-%m-%d-%H%M%S')" \
 -t silent-guard-api:candidate \
 ./silent-api
```

**Building a fat jar with all Spring classes included**

- Run: `./mvnw package spring-boot:repackage -DskipTests`

**Run GraalVM agent to generate hints**

- Run:

```sh
java -jar \
  -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image \
  silentguardapi-0.0.1-SNAPSHOT.jar
```

That's it!

## Deploying manually

1. Connect into the server via SSH
2. Run deploy scripts

Here are a few steps:

```bash
export SERVER_IP=
export SERVER_ADDRESS=

npm ci --ignore-scripts --no-update-notifier --omit=dev
export VITE_BACKEND_SERVER=$SERVER_ADDRESS/server
npm run build
zip -r "client_$VERSION.zip" dist/
scp "client_$VERSION.zip" root@$SERVER_IP:/root/
```

## Running with Docker

**DB:**

```bash
docker run -d -p 5432:5432 --rm \
  --name silent-db \
  -e POSTGRES_DB=$POSTGRES_DB \
  -e POSTGRES_USER=$POSTGRES_USER \
  -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
  postgres:15.8-bookworm
```

**API:**

```bash
docker run -p 8080:8080 --rm \
  --name silent-api \
  -e AUTH_DOMAIN=$AUTH_DOMAIN \
  -e API_IDENTIFIER=$API_IDENTIFIER \
  -e POSTGRES_DB=$POSTGRES_DB \
  -e POSTGRES_USER=$POSTGRES_USER \
  -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
  -e POSTGRES_PORT=$POSTGRES_PORT \
  -e POSTGRES_HOST=$POSTGRES_HOST \
  -e CORS_ALLOWED_ORIGINS=$CORS_ALLOWED_ORIGINS \
  -e MAILGUN_APIKEY=$MAILGUN_APIKEY \
  -e TARGET_ENV=$TARGET_ENV \
  -e API_LOGGING_LEVEL=$API_LOGGING_LEVEL \
  silent-guard-api:candidate
```

Build Cloud Native: `./mvnw -B package -Pnative -DskipTests`

**Client:**

The frontend app will run on Nginx.

## Interacting with GitHub Package and Container Registry

**Login in:**

```
export CR_PAT=YOUR_TOKEN
echo $CR_PAT | docker login ghcr.io -u rmcampos --password-stdin
```

**Pulling images:**

```sh
docker pull ghcr.io/ricardo-campos-org/react-typescript-todolist/client:50
docker pull ghcr.io/ricardo-campos-org/react-typescript-todolist/server:50
```

**Pushing images:**
```sh
docker push ghcr.io/ricardo-campos-org/react-typescript-todolist/server:316
```

- Get container IP

```sh
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' db
```

- Get reason of health check failing on a container
```sh
docker inspect --format "{{json .State.Health}}" container_name_or_id | jq

# Or to see it live
while true; do clear; docker inspect --format "{{json .State.Health}}" server | jq; sleep 1; done
```

- Extract Env value from docker image (without running)
```bash
docker inspect client:candidate | jq -r '.[0].Config.Env[] | select(startswith("SOURCE_PR="))' | sed -n 's/SOURCE_PR=\(v[0-9]*\).*/\1/p'
```

## Restoring DB for testing

```bash
docker run \
 --name my-postgres-db \
 -e POSTGRES_USER="<user>" \
 -e POSTGRES_PASSWORD='<password>' \
 -e POSTGRES_DB="<db-name>" \
 -p 5432:5432 \
 -d postgres
```

```bash
docker exec -i my-postgres-db pg_restore -U <user> -d <db-name> < 2025-04-26T20_00_00.114Z.sql

```

<div className="py-5 text-center">
  <div className="d-block d-sm-none bg-primary text-white p-3">XS (Extra Small): less than 576px</div>
  <div className="d-none d-sm-block d-md-none bg-secondary text-white p-3">SM (Small): ≥576px</div>
  <div className="d-none d-md-block d-lg-none bg-success text-white p-3">MD (Medium): ≥768px</div>
  <div className="d-none d-lg-block d-xl-none bg-warning text-dark p-3">LG (Large): ≥992px</div>
  <div className="d-none d-xl-block d-xxl-none bg-danger text-white p-3">XL (Extra Large): ≥1200px</div>
  <div className="d-none d-xxl-block bg-dark text-white p-3">XXL (Extra Extra Large): ≥1400px</div>
</div>
