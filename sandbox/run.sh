

# Obtém o diretório onde o script está localizado
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Criar as pastas com permissões
mkdir -m 777 "$SCRIPT_DIR"/.docker
mkdir -m 777 "$SCRIPT_DIR"/.docker/keycloak

if ! docker compose  -f "$SCRIPT_DIR"/app/docker-compose.yml up -d; then
  echo "Falha ao inicializar os containers"
  exit 1
fi

echo "inicializando os containers"