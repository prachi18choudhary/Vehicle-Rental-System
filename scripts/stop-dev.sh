#!/usr/bin/env bash
# Stop all locally-running Spring Boot services + infra.
set -euo pipefail

cd "$(dirname "$0")/.."

echo ">>> Killing Maven Spring Boot processes..."
pkill -f "spring-boot:run" || true

echo ">>> Stopping Docker infra..."
docker compose down

echo ">>> Done."
