#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ ! -f .env ]]; then echo "Falta Proyecto-Worker/.env. Copia .env.example a .env y complétalo." >&2; exit 1; fi
set -a
# shellcheck disable=SC1091
source ./.env
set +a
: "${FLOWABLE_URL:?Configura FLOWABLE_URL en .env}"
: "${WS_URL:?Configura WS_URL en .env}"
if [[ -z "${FLOWABLE_TOKEN:-}" && ( -z "${FLOWABLE_USER:-}" || -z "${FLOWABLE_PASSWORD:-}" ) ]]; then
  echo "Configura FLOWABLE_TOKEN o FLOWABLE_USER y FLOWABLE_PASSWORD en .env." >&2; exit 1
fi
javac PeluditosApp.java
exec java PeluditosApp
