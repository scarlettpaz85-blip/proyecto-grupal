#!/usr/bin/env bash
# Carga las variables de Proyecto-WS/.env y ejecuta el servicio.
set -euo pipefail
cd "$(dirname "$0")"

if [[ ! -f .env ]]; then
  if [[ -f .env.example ]]; then
    cp .env.example .env
    echo "Se creó .env desde .env.example. Edita el archivo y configura la clave real de SQL Server; luego vuelve a ejecutar este script." >&2
    exit 1
  fi
  echo "No existe .env ni .env.example." >&2
  exit 1
fi

set -a
# .env debe contener asignaciones KEY=VALUE, sin comandos shell.
source ./.env
set +a

if [[ -z "${PELUDITOS_DB_PASSWORD:-}" || "$PELUDITOS_DB_PASSWORD" == "CAMBIAR_POR_SECRETO_REAL" ]]; then
  echo "Configura PELUDITOS_DB_PASSWORD en Proyecto-WS/.env antes de iniciar." >&2
  exit 1
fi

chmod +x ./gradlew
./gradlew run
