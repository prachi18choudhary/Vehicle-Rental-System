#!/usr/bin/env bash
# Start the local dev stack: infra in Docker + Spring Boot services in background.
# Frontend must be started separately: cd frontend && npm run dev
set -euo pipefail

cd "$(dirname "$0")/.."

mkdir -p logs

if ! command -v mvn >/dev/null 2>&1; then
  if [ -d "$HOME/.m2/wrapper/dists/apache-maven-3.8.7-bin" ]; then
    export PATH="$(find "$HOME/.m2/wrapper/dists/apache-maven-3.8.7-bin" -name 'mvn' -type f | head -1 | xargs dirname):$PATH"
  fi
fi

echo ">>> Starting MySQL + RabbitMQ..."
docker compose up -d mysql rabbitmq

echo ">>> Waiting 15s for MySQL/RabbitMQ to become healthy..."
sleep 15

services=(eureka-server auth-service vehicle-service booking-service payment-service notification-service api-gateway)
echo ">>> Starting Spring Boot services..."
for s in "${services[@]}"; do
  echo "  - $s"
  ( cd backend && nohup mvn -q -pl "$s" spring-boot:run > "../logs/$s.log" 2>&1 & )
  sleep 4
done

echo ">>> All services starting (logs in ./logs/)."
echo ">>> Eureka:    http://localhost:8761"
echo ">>> Gateway:   http://localhost:8080"
echo ">>> RabbitMQ:  http://localhost:15672 (guest/guest)"
echo ">>> Now run:   cd frontend && npm install && npm run dev"
