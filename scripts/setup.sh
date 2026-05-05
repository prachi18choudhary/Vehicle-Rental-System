#!/usr/bin/env bash
# Vehicle Rental System — one-shot prerequisite installer for macOS
set -euo pipefail

GREEN="\033[0;32m"; YELLOW="\033[0;33m"; RED="\033[0;31m"; NC="\033[0m"
log() { echo -e "${GREEN}[setup]${NC} $1"; }
warn() { echo -e "${YELLOW}[setup]${NC} $1"; }
err() { echo -e "${RED}[setup]${NC} $1"; }

# 1. Homebrew
if ! command -v brew >/dev/null 2>&1; then
  log "Installing Homebrew..."
  /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
  echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> "$HOME/.zprofile"
  eval "$(/opt/homebrew/bin/brew shellenv)"
else
  log "Homebrew already installed"
fi

# 2. Java 17
if ! /usr/libexec/java_home -v 17 >/dev/null 2>&1; then
  log "Installing Java 17..."
  brew install openjdk@17
  sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk || true
else
  log "Java 17 already installed"
fi

# 3. Maven
if ! command -v mvn >/dev/null 2>&1; then
  log "Installing Maven..."
  brew install maven
else
  log "Maven already installed"
fi

# 4. Node 20
if ! command -v node >/dev/null 2>&1 || [[ "$(node -v 2>/dev/null)" != v20* && "$(node -v 2>/dev/null)" != v22* ]]; then
  log "Installing Node 20 LTS..."
  brew install node@20
  brew link --overwrite --force node@20
else
  log "Node $(node -v) already installed"
fi

# 5. Docker Desktop (cask)
if ! command -v docker >/dev/null 2>&1; then
  warn "Docker Desktop is not installed. Installing via cask (you may be prompted for sudo)..."
  brew install --cask docker
  warn "After install completes, OPEN Docker Desktop from /Applications and accept terms."
else
  log "Docker already installed: $(docker --version)"
fi

# 6. Verify
log "----- Versions -----"
java -version 2>&1 || true
mvn -version || true
node -v || true
docker --version || true
git --version || true

log "Setup complete!"
log "Next steps:"
echo "   cp .env.example .env       # then fill in your Razorpay keys"
echo "   docker compose up --build  # launches the entire stack"
